package com.netraa.aopdemo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.netraa.aopdemo.dto.LogReqDto;
import com.netraa.aopdemo.entity.Log;
import com.netraa.aopdemo.entity.Service;

/** Stage 1: receive logs and hand them to the minute aggregator. */
@Component
public class LogService {

    private final ServiceService serviceService;
    private final MinuteAggregator minuteAggregator;

    public LogService(ServiceService serviceService, MinuteAggregator minuteAggregator) {
        this.serviceService = serviceService;
        this.minuteAggregator = minuteAggregator;
    }

    public List<Log> getLogs() {
        return new ArrayList<>();
    }

    public void saveLog(LogReqDto logReqDto) {
        minuteAggregator.add(convertToLog(logReqDto));
    }

    public void saveLogs(List<LogReqDto> logs) {
        for (LogReqDto dto : logs) {
            minuteAggregator.add(convertToLog(dto));
        }
    }

    private Log convertToLog(LogReqDto dto) {
        Log log = new Log();
        log.setTimestamp(dto.getTimestamp());
        log.setResponseTimeMs(dto.getResponseTimeMs());
        log.setLevel(dto.getLevel());
        Service service = serviceService.saveService(dto.getService());
        log.setService(service);
        log.setStatusCode(dto.getStatusCode());
        return log;
    }
}
