package com.netraa.aopdemo.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.netraa.aopdemo.document.Metric;
import com.netraa.aopdemo.entity.Log;

/** Pure aggregation math, no I/O. */
@Service
public class AggregateService {

    public Instant minuteOf(Instant ts) {
        return ts.truncatedTo(ChronoUnit.MINUTES);
    }

    public Metric newMinuteBucket(Log log) {
        Instant start = minuteOf(log.getTimestamp());
        Metric b = new Metric();
        b.setId(Metric.idFor(Metric.MINUTE, log.getService().getId(), start));
        b.setGranularity(Metric.MINUTE);
        b.setServiceId(log.getService().getId());
        b.setServiceName(log.getService().getName());
        b.setBucketStart(start);
        return b;
    }

    public void addLog(Metric b, Log log) {
        int rt = log.getResponseTimeMs() == null ? 0 : log.getResponseTimeMs();
        b.setReqCount(b.getReqCount() + 1);
        b.setTotalResponseTimeMs(b.getTotalResponseTimeMs() + rt);
        b.setMinResponseTimeMs(Math.min(b.getMinResponseTimeMs(), rt));
        b.setMaxResponseTimeMs(Math.max(b.getMaxResponseTimeMs(), rt));
        if (log.getStatusCode() != null && log.getStatusCode() >= 500) {
            b.setErrorCount(b.getErrorCount() + 1);
        }
        b.recomputeAvg();
    }
}
