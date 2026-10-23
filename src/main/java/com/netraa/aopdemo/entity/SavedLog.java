package com.netraa.aopdemo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "logspermin")
public class SavedLog {


    @Id
    private String id;
    private Long serviceId;
    private Double averageResponseTimePerMin;
    private Integer resonseCount;

    public String getId() {
        return id;
    }



    public void setId(String id) {
        this.id = id;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Double getAverageResponseTimePerMin() {
        return averageResponseTimePerMin;
    }

    public void setAverageResponseTimePerMin(Double averageResponseTimePerMin) {
        this.averageResponseTimePerMin = averageResponseTimePerMin;
    }

    public Integer getResonseCount() {
        return resonseCount;
    }

    public void setResonseCount(Integer resonseCount) {
        this.resonseCount = resonseCount;
    }
}
