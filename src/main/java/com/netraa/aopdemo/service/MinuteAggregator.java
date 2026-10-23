package com.netraa.aopdemo.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.netraa.aopdemo.document.Metric;

import com.netraa.aopdemo.entity.Log;
import com.netraa.aopdemo.repository.MetricRepository;

/**
 * Stage 2: keeps open per-minute buckets in memory and writes closed
 * minutes to Elasticsearch.
 */
@Component
public class MinuteAggregator {

    private final ConcurrentHashMap<String, Metric> buckets = new ConcurrentHashMap<>();
    private final AggregateService aggregateService;
    private final MetricRepository repo;

    public MinuteAggregator(AggregateService aggregateService, MetricRepository repo) {
        this.aggregateService = aggregateService;
        this.repo = repo;
    }

    public void add(Log log) {
        if (log.getTimestamp() == null || log.getService() == null) {
            return;
        }
        Metric probe = aggregateService.newMinuteBucket(log);
        buckets.compute(probe.getId(), (id, existing) -> {
            Metric b = existing != null ? existing : probe;
            aggregateService.addLog(b, log);
            return b;
        });
    }

    /** Runs 5 seconds after every minute and flushes minutes that are finished. */
    @Scheduled(cron = "5 * * * * *")
    public void flush() {
        flushOlderThan(Instant.now().minusSeconds(5).truncatedTo(ChronoUnit.MINUTES));
    }

    /** Flush every bucket whose minute starts before the cutoff. Also used in tests. */
    public void flushOlderThan(Instant cutoff) {
        for (Metric b : buckets.values()) {
            if (!b.getBucketStart().isBefore(cutoff)) {
                continue;
            }
            // remove first so logs arriving now start a fresh bucket instead of being lost
            if (buckets.remove(b.getId(), b)) {
                // a late log may already have been saved for this minute: merge instead of overwrite
                repo.findById(b.getId()).ifPresent(old -> {
                    b.merge(old);
                });
                repo.save(b);
            }
        }
    }
}
