package com.netraa.aopdemo.service;



import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.netraa.aopdemo.document.Metric;

import com.netraa.aopdemo.repository.MetricRepository;

/**
 * Stage 3: builds HOUR from MINUTE, DAY from HOUR, MONTH from DAY.
 * Day and month boundaries use ZONE. Change it if you want UTC.
 */
@Component
public class RollupService {

    public static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");

    private final MetricRepository repo;

    public RollupService(MetricRepository repo) {
        this.repo = repo;
    }

    @Scheduled(cron = "0 2 * * * *", zone = "Asia/Kolkata")
    public void hourly() {
        Instant to = Instant.now().truncatedTo(ChronoUnit.HOURS);
        rollup(Metric.MINUTE, Metric.HOUR, to.minus(1, ChronoUnit.HOURS), to);
    }

    @Scheduled(cron = "0 10 0 * * *", zone = "Asia/Kolkata")
    public void daily() {
        ZonedDateTime to = ZonedDateTime.now(ZONE).truncatedTo(ChronoUnit.DAYS);
        rollup(Metric.HOUR, Metric.DAY, to.minusDays(1).toInstant(), to.toInstant());
    }

    @Scheduled(cron = "0 20 0 1 * *", zone = "Asia/Kolkata")
    public void monthly() {
        ZonedDateTime to = ZonedDateTime.now(ZONE).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        rollup(Metric.DAY, Metric.MONTH, to.minusMonths(1).toInstant(), to.toInstant());
    }

    /**
     * Reads all src buckets in [from, to), groups them per service and
     * writes one dst bucket per service starting at "from". Safe to re-run:
     * the id is deterministic, so the result is overwritten, not duplicated.
     */
    public List<Metric> rollup(String src, String dst, Instant from, Instant to) {
        List<Metric> rows =
                repo.findByGranularityAndBucketStartGreaterThanEqualAndBucketStartLessThan(src, from, to);

        Map<Long, List<Metric>> byService =
                rows.stream().collect(Collectors.groupingBy(Metric::getServiceId));

        List<Metric> out = new ArrayList<>();
        byService.forEach((serviceId, list) -> {
            Metric r = new Metric();
            r.setId(Metric.idFor(dst, serviceId, from));
            r.setGranularity(dst);
            r.setServiceId(serviceId);
            r.setServiceName(list.get(0).getServiceName());
            r.setBucketStart(from);
            list.forEach(r::merge);
            out.add(r);
        });
        repo.saveAll(out);
        return out;
    }
}
