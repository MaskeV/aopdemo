package com.netraa.aopdemo.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.Instant;

@Document(indexName = "logsperhour")
public class LogsPerHour {

    @Id
    private String id;
    private Long serviceId;
    private Instant startOfHour;
    private Instant endOfHour;
    private Double averagePerHour;
    private Integer request_count;



}
