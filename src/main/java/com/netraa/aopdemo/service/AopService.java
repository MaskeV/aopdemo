package com.netraa.aopdemo.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.netraa.aopdemo.document.Metric;
import com.netraa.aopdemo.repository.MetricRepository;

/** Read side: query stored buckets. */
@Service
public class AopService {

    private final MetricRepository repo;

    public AopService(MetricRepository repo) {
        this.repo = repo;
    }

    public List<Metric> getMetrics(String serviceName, String granularity, Instant from, Instant to) {
        return repo.findByServiceNameAndGranularityAndBucketStartGreaterThanEqualAndBucketStartLessThanOrderByBucketStartAsc(
                serviceName, granularity.toUpperCase(), from, to);
    }
}
