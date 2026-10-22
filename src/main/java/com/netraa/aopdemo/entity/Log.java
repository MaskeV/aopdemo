package com.netraa.aopdemo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.Instant;
import java.time.LocalDateTime;

@Document(indexName = "logs")
public class Log {


    @Id
    private String id;
    private Instant timestamp;
    private String level;
    private Service service;
    private Integer responseTimeMs;
    private Integer statusCode;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Integer getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(Integer responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", level='" + level + '\'' +
                ", service=" + service +
                ", responseTime=" + responseTimeMs +
                ", statusCode=" + statusCode +
                '}';
    }
}
