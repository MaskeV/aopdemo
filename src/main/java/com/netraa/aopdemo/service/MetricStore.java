package com.netraa.aopdemo.service;

import com.netraa.aopdemo.document.Metric;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MetricStore {

    private final ElasticsearchOperations ops;

    public MetricStore(ElasticsearchOperations ops) {
        this.ops = ops;
    }

    public static IndexCoordinates indexFor(String granularity, Instant bucketStart) {
        if (Metric.MINUTE.equals(granularity)) {

            String day = DateTimeFormatter.ofPattern("yyyy.MM.dd")
                    .withZone(ZoneOffset.UTC).format(bucketStart);
            return IndexCoordinates.of("metrics-minute" );
        }
        return IndexCoordinates.of("metrics-" + granularity.toLowerCase());
    }

    public void save(Metric b) {
        IndexCoordinates idx = indexFor(b.getGranularity(), b.getBucketStart());
        IndexOperations io = ops.indexOps(idx);
        if (!io.exists()) {
            io.create();
            io.putMapping(io.createMapping(Metric.class));// makes sure bucketStart is a date field
        }
        ops.save(b, idx);
    }

    public Optional<Metric> findById(String id, String granularity, Instant bucketStart) {

        IndexCoordinates idx = indexFor(granularity,bucketStart);
        if(!ops.indexOps(idx).exists()){
            return Optional.empty();
        }
        return Optional.ofNullable(ops.get(id, Metric.class, indexFor(granularity, bucketStart)));
    }

    /** Read buckets of one granularity in [from, to). */
    public List<Metric> find(String granularity, String serviceName, Instant from, Instant to) {
        Criteria c = new Criteria("bucketStart").greaterThanEqual(from).and(new Criteria("bucketStart").lessThan(to));
        if (serviceName != null) {
            c = c.and(new Criteria("serviceName").is(serviceName));
        }
        CriteriaQuery q = new CriteriaQuery(c);
        q.setPageable(PageRequest.of(0, 10000));


        List<IndexCoordinates> indices = new ArrayList<>();
        if (Metric.MINUTE.equals(granularity)) {
            for (Instant d = from.truncatedTo(ChronoUnit.DAYS); d.isBefore(to); d = d.plus(1, ChronoUnit.DAYS)) {
                indices.add(indexFor(granularity, d));
            }
        } else {
            indices.add(indexFor(granularity, from));
        }

        List<Metric> out = new ArrayList<>();
        for (IndexCoordinates idx : indices) {
            if (!ops.indexOps(idx).exists()) continue;
            ops.search(q, Metric.class, idx).forEach(hit -> out.add(hit.getContent()));
        }
        return out;
    }

    public void saveAll(List<Metric> out) {
        for(Metric m:out){
            save(m);
        }
    }
}