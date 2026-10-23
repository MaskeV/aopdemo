package com.netraa.aopdemo.controller;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.netraa.aopdemo.document.Metric;
import com.netraa.aopdemo.service.AopService;
import com.netraa.aopdemo.service.MinuteAggregator;
import com.netraa.aopdemo.service.ScheduleService;

@RestController
@RequestMapping("/api/metrics")
public class AopController {

    private final AopService aopService;
    private final MinuteAggregator minuteAggregator;
    private final ScheduleService scheduleService;

    public AopController(AopService aopService, MinuteAggregator minuteAggregator, ScheduleService scheduleService) {
        this.aopService = aopService;
        this.minuteAggregator = minuteAggregator;
        this.scheduleService = scheduleService;
    }

    /** GET /api/metrics?service=payments&granularity=HOUR&from=2026-10-22T00:00:00Z&to=2026-10-23T00:00:00Z */
    @GetMapping
    public List<Metric> getMetrics(
            @RequestParam String service,
            @RequestParam String granularity,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate from,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate to) {

        // Start of the day (00:00:00 UTC)
        Instant fromInstant = from.atStartOfDay().toInstant(ZoneOffset.UTC);

        // End of the day (23:59:59.999999999 UTC) - successfully works on LocalDate
        Instant toInstant = to.atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC);

        return aopService.getMetrics(service, granularity, fromInstant, toInstant);
    }


    @GetMapping("/admin/flush")
    public String flushNow() {
        minuteAggregator.flushOlderThan(Instant.now().plusSeconds(120));
        return "flushed";
    }


    @GetMapping("/admin/rollup")
    public List<Metric> rollup(@RequestParam String src, @RequestParam String dst,
                               @RequestParam Instant from, @RequestParam Instant to) {
        return scheduleService.rollup(src.toUpperCase(), dst.toUpperCase(), from, to);
    }
}
