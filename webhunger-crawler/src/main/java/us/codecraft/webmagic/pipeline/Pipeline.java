package us.codecraft.webmagic.pipeline;

import me.shenchao.webhunger.entity.webmagic.ResultItems;
import us.codecraft.webmagic.LifeCycle;

/**
 * 重构Pipeline去掉ResultItem类
 *
 * Pipeline is the persistent and offline process part of crawler.<br>
 * The interface Pipeline can be implemented to customize ways of persistent.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public interface Pipeline {

    /**
     * Process extracted results.
     *
     * @param resultItems resultItems
     * @param task task
     */
    void process(ResultItems resultItems, LifeCycle task);
}
