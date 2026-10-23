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


@Component
public class ScheduleService {

    public static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");

   // private final MetricRepository repo;
    private final MetricStore metricStore;

    public ScheduleService(MetricStore metricStore) {
        this.metricStore = metricStore;
    }

    @Scheduled(cron = "0 */5 * * * *", zone = "Asia/Kolkata")
    public void hourly() {
        Instant to = Instant.now().truncatedTo(ChronoUnit.HOURS);
        rollup(Metric.MINUTE, Metric.HOUR, to.minus(1, ChronoUnit.HOURS), to);
        System.out.println("from hourly");
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


    public List<Metric> rollup(String src, String dst, Instant from, Instant to) {
        List<Metric> rows =
             //   repo.findByGranularityAndBucketStartGreaterThanEqualAndBucketStartLessThan(src, from, to);
        metricStore.find(src,null,from,to);

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
            //list.forEach(r::merge);

            r.setReqCount(list.stream().mapToLong(Metric::getReqCount).sum());
            r.setTotalResponseTimeMs(list.stream().mapToLong(Metric::getTotalResponseTimeMs).sum());
            r.setAvgResponseTimeMs((Metric.weightedAvg(list)));
            r.setMaxResponseTimeMs(list.stream().mapToInt(Metric::getMaxResponseTimeMs).max().getAsInt());
            r.setMinResponseTimeMs(list.stream().mapToInt(Metric::getMinResponseTimeMs).min().getAsInt());
            r.setErrorCount(list.stream().mapToLong(Metric::getErrorCount).sum());

            out.add(r);

        });
        metricStore.saveAll(out);
        return out;
    }
}