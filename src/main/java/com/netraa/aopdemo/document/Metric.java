package com.netraa.aopdemo.document;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * One aggregated bucket for one service and one time window.
 * granularity = MINUTE | HOUR | DAY | MONTH.
 *
 * Sum and count are stored (not only the average) so that higher
 * levels can be computed exactly: avg = sum(total) / sum(count).
 */
@Document(indexName = "metrics")
public class Metric {

    public static final String MINUTE = "MINUTE";
    public static final String HOUR = "HOUR";
    public static final String DAY = "DAY";
    public static final String MONTH = "MONTH";

    @Id
    private String id; // granularity_serviceId_epochSeconds

    @Field(type = FieldType.Keyword)
    private String granularity;

    @Field(type = FieldType.Long)
    private Long serviceId;

    @Field(type = FieldType.Keyword)
    private String serviceName;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Instant bucketStart;

    private long reqCount;
    private long totalResponseTimeMs;
    private double avgResponseTimeMs;
    private int minResponseTimeMs = Integer.MAX_VALUE;
    private int maxResponseTimeMs = Integer.MIN_VALUE;
    private long errorCount;

    public static String idFor(String granularity, Long serviceId, Instant bucketStart) {
        return granularity + "_" + serviceId + "_" + bucketStart.getEpochSecond();
    }

    /** Add another bucket's numbers into this one (used for merging and rollups). */
    public void merge(Metric other) {
        this.reqCount += other.reqCount;
        this.totalResponseTimeMs += other.totalResponseTimeMs;
        this.minResponseTimeMs = Math.min(this.minResponseTimeMs, other.minResponseTimeMs);
        this.maxResponseTimeMs = Math.max(this.maxResponseTimeMs, other.maxResponseTimeMs);
        this.errorCount += other.errorCount;
        recomputeAvg();
    }

    public void recomputeAvg() {
        this.avgResponseTimeMs = reqCount == 0 ? 0 : totalResponseTimeMs / (double) reqCount;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getGranularity() { return granularity; }
    public void setGranularity(String granularity) { this.granularity = granularity; }
    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public Instant getBucketStart() { return bucketStart; }
    public void setBucketStart(Instant bucketStart) { this.bucketStart = bucketStart; }
    public long getReqCount() { return reqCount; }
    public void setReqCount(long reqCount) { this.reqCount = reqCount; }
    public long getTotalResponseTimeMs() { return totalResponseTimeMs; }
    public void setTotalResponseTimeMs(long totalResponseTimeMs) { this.totalResponseTimeMs = totalResponseTimeMs; }
    public double getAvgResponseTimeMs() { return avgResponseTimeMs; }
    public void setAvgResponseTimeMs(double avgResponseTimeMs) { this.avgResponseTimeMs = avgResponseTimeMs; }
    public int getMinResponseTimeMs() { return minResponseTimeMs; }
    public void setMinResponseTimeMs(int minResponseTimeMs) { this.minResponseTimeMs = minResponseTimeMs; }
    public int getMaxResponseTimeMs() { return maxResponseTimeMs; }
    public void setMaxResponseTimeMs(int maxResponseTimeMs) { this.maxResponseTimeMs = maxResponseTimeMs; }
    public long getErrorCount() { return errorCount; }
    public void setErrorCount(long errorCount) { this.errorCount = errorCount; }
}
