package me.shenchao.webhunger.crawler.caller;

import com.alibaba.fastjson.JSON;
import me.shenchao.webhunger.constant.ZookeeperPathConsts;
import me.shenchao.webhunger.crawler.dominate.DistributedSiteDominate;
import me.shenchao.webhunger.crawler.listener.BaseSpiderListener;
import me.shenchao.webhunger.crawler.util.HostSnapshotHelper;
import me.shenchao.webhunger.dto.HostCrawlingSnapshotDTO;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.webmagic.Site;
import me.shenchao.webhunger.rpc.api.crawler.CrawlerCallable;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 爬虫控制器，实现爬虫控制接口，接受控制模块RPC调用
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class RpcCrawlerCaller implements CrawlerCallable {

    private static final Logger logger = LoggerFactory.getLogger(RpcCrawlerCaller.class);

    private ZooKeeper zooKeeper;

    private DistributedSiteDominate siteDominate;

    private BaseSpiderListener spiderListener;

    public RpcCrawlerCaller(DistributedSiteDominate siteDominate, BaseSpiderListener spiderListener, ZooKeeper zooKeeper) {
        this.siteDominate = siteDominate;
        this.zooKeeper = zooKeeper;
        this.spiderListener = spiderListener;
    }

    @Override
    public void run() {
        try {
            // 修改zookeeper中节点状态
            zooKeeper.setData(ZookeeperPathConsts.getCrawlerNodePath(), "1".getBytes(), -1);
            logger.info("爬虫节点开始运行......");
            // 监听待爬列表开始爬取操作
            watchCrawlingHostListChanged();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分布式爬取中，由控制模块直接通过向redis中添加站点，并通过zookeeper事件监听机制，触发爬虫节点对站点进行爬取，所以无须调用此方法。
     * 当前阶段，分布式爬取不支持指定爬虫节点爬取具体某几个站点，所以不支持该方法，当前只支持所有爬虫节点爬取所有站点
     *
     * @param hosts hosts
     */
    @Override
    public void crawl(List<Host> hosts) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void stop(String hostId) {
        siteDominate.addToStoppingList(hostId);
    }

    /**
     * 分布式爬取中，借助zookeeper监听来判断爬取是否结束，无须调用该方法
     */
    @Override
    public HostCrawlingSnapshotDTO checkCrawledCompleted(String hostId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HostCrawlingSnapshotDTO createSnapshot(String hostId) {
        throw new UnsupportedOperationException();
    }

    /**
     * 监控正在爬取的网站列表，一旦发生变化，同步更新本地爬取列表
     */
    private void watchCrawlingHostListChanged() {
        try {
            List<String> nodeList = zooKeeper.getChildren(ZookeeperPathConsts.CRAWLING_HOST, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    watchCrawlingHostListChanged();
                }
            });
            List<Site> newSiteList = new ArrayList<>();
            for (String nodeName : nodeList) {
                String nodePath = ZookeeperPathConsts.DETAIL_HOST + "/" + nodeName;
                byte[] hostBytes = zooKeeper.getData(nodePath, false, null);
                Host host = JSON.parseObject(new String(hostBytes), Host.class);
                Site site = new Site(host);
                newSiteList.add(site);
            }
            siteDominate.updateLocalCrawlingSiteList(newSiteList);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
