package com.netraa.aopdemo.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.netraa.aopdemo.document.Metric;



public interface MetricRepository extends ElasticsearchRepository<Metric, String> {

    List<Metric> findByGranularityAndBucketStartGreaterThanEqualAndBucketStartLessThan(
            String granularity, Instant from, Instant to);

    List<Metric> findByServiceNameAndGranularityAndBucketStartGreaterThanEqualAndBucketStartLessThanOrderByBucketStartAsc(
            String serviceName, String granularity, Instant from, Instant to);
}
