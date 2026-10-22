package com.netraa.aopdemo.service;

import com.netraa.aopdemo.dto.LogReqDto;
import com.netraa.aopdemo.entity.Log;
import com.netraa.aopdemo.entity.Service;
import org.springframework.stereotype.Component;


import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LogService {

    private  final  ServiceService serviceService;
    private final AggregateService aggregateService;

    public LogService(ServiceService serviceService, AggregateService aggregateService) {
        this.serviceService = serviceService;
        this.aggregateService = aggregateService;
    }

    public List<Log> getLogs() {
        return  new ArrayList<>();
    }

    public void saveLog(LogReqDto logReqDto) {

            Log log = convertToLog(logReqDto);
        System.out.println(log);

            int count =0;
            int total=0;

            if(count==60){
              double avgRestimeperMin = aggregateService.avgRestimeperMin(total,count);
               count=0;
            }else {
              total += logReqDto.getResponseTimeMs();
                count++;
            }




    }

    public void saveLogs(List<LogReqDto> logs) {
        for(LogReqDto logReqDto : logs){
            Log log = convertToLog(logReqDto);
            System.out.println(log);
        }
    }

    private Log convertToLog(LogReqDto logReqDto){
        Log log = new Log();
        log.setId(logReqDto.getId());
        log.setTimestamp(logReqDto.getTimestamp());
        log.setResponseTimeMs(logReqDto.getResponseTimeMs());
        log.setLevel(logReqDto.getLevel());
        Service service = serviceService.saveService(logReqDto.getService());
        log.setService(service);
        log.setStatusCode(logReqDto.getStatusCode());

        return log;
    }
}
