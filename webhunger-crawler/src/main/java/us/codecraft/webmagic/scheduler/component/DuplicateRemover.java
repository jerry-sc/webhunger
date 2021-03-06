package us.codecraft.webmagic.scheduler.component;

import me.shenchao.webhunger.entity.webmagic.Request;
import us.codecraft.webmagic.LifeCycle;

/**
 * 去重算法单独抽象为一个接口，以后若要替换算法，实现该接口
 * Remove duplicate requests.
 * @author code4crafer@gmail.com
 * @since 0.5.1
 */
public interface DuplicateRemover {
    /**
     *
     * Check whether the request is duplicate.
     *
     * @param request request
     * @param task task
     * @return true if is duplicate
     */
    public boolean isDuplicate(Request request, LifeCycle task);

    /**
     * Reset duplicate extract.
     * @param task task
     */
    default void resetDuplicateCheck(LifeCycle task) {}

    /**
     * Get TotalRequestsCount for Monitor.
     * @param task task
     * @return number of total request
     */
    default int getTotalRequestsCount(LifeCycle task) {return 0;}

}
