package me.shenchao.webhunger;

import me.shenchao.webhunger.config.ControlConfig;
import me.shenchao.webhunger.control.controller.ControllerFactory;
import me.shenchao.webhunger.util.common.SystemUtils;
import me.shenchao.webhunger.web.WebConsoleStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * 控制模块启动类
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class ControlBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(ControlBootstrap.class);

    private static final String CONF_NAME = "webhunger.conf";

    private ControlConfig controlConfig;

    private void parseControlConfig() {
        controlConfig = new ControlConfig();
        try {
            controlConfig.parse(SystemUtils.getWebHungerConfigDir() + File.separator + CONF_NAME);
        } catch (IOException e) {
            logger.error("控制模块配置文件读取失败，程序退出......", e);
            System.exit(1);
        }
        // log config info
        logger.info("配置解析完成，使用如下参数启动控制程序：");
        logger.info("Distributed: {}", controlConfig.isDistributed());
        logger.info("Task Accessor Jar Dir: {}", controlConfig.getTaskAccessorJarDir());
        logger.info("Task Accessor Class: {}", controlConfig.getTaskAccessorClass());
        logger.info("Jetty Port: {}", controlConfig.getPort());
        logger.info("Web Context Path: {}", controlConfig.getContentPath());
    }

    public void start() {
        logger.info("控制模块开始启动......");
        // 解析配置
        parseControlConfig();
        // 初始化中央控制器
        ControllerFactory.initController(controlConfig);

        // 启动web控制台
        try {
            new WebConsoleStarter().startServer(controlConfig.getPort(), controlConfig.getContentPath());
        } catch (Exception e) {
            logger.error("Web控制台启动失败，程序退出......", e);
            System.exit(1);
        }
        logger.info("Web控制台启动完成......");
        logger.info("WebHunger WebConsole available at http://localhost:5572/webhunger/task/list");
    }

    public static void main(String[] args) {
        new ControlBootstrap().start();
    }
}
