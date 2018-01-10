package me.shenchao.webhunger.crawler.util;

import me.shenchao.webhunger.dto.HostSnapshotDTO;
import me.shenchao.webhunger.entity.SiteStatusStatistics;

/**
 * 辅助类，用于创建站点快照
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class HostSnapshotHelper {

    public static HostSnapshotDTO create(String siteId, SiteStatusStatistics siteStatusStatistics) {
        return new HostSnapshotDTO.Builder(siteId).totalPageNum(siteStatusStatistics.getTotalPageNum())
                .leftPageNum(siteStatusStatistics.getLeftPageNum()).successPageNum(siteStatusStatistics.getSuccessPageNum().get())
                .errorPageNum(siteStatusStatistics.getErrorPageNum().get()).startTime(siteStatusStatistics.getStartTime())
                .endTime(siteStatusStatistics.getEndTime()).errorPages(siteStatusStatistics.getErrorRequests())
                .build();
    }

}
