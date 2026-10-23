package com.netraa.aopdemo.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.netraa.aopdemo.document.Metric;
import com.netraa.aopdemo.service.AopService;
import com.netraa.aopdemo.service.MinuteAggregator;
import com.netraa.aopdemo.service.RollupService;

@RestController
@RequestMapping("/api/metrics")
public class AopController {

    private final AopService aopService;
    private final MinuteAggregator minuteAggregator;
    private final RollupService rollupService;

    public AopController(AopService aopService, MinuteAggregator minuteAggregator, RollupService rollupService) {
        this.aopService = aopService;
        this.minuteAggregator = minuteAggregator;
        this.rollupService = rollupService;
    }

    /** GET /api/metrics?service=payments&granularity=HOUR&from=2026-10-22T00:00:00Z&to=2026-10-23T00:00:00Z */
    @GetMapping
    public List<Metric> getMetrics(@RequestParam String service,
                                         @RequestParam String granularity,
                                         @RequestParam Instant from,
                                         @RequestParam Instant to) {
        return aopService.getMetrics(service, granularity, from, to);
    }

    /** Testing helper: flush all open minute buckets now. */
    @GetMapping("/admin/flush")
    public String flushNow() {
        minuteAggregator.flushOlderThan(Instant.now().plusSeconds(120));
        return "flushed";
    }

    /** Testing helper: rebuild a rollup for a time range, e.g. src=MINUTE&dst=HOUR. */
    @GetMapping("/admin/rollup")
    public List<Metric> rollup(@RequestParam String src, @RequestParam String dst,
                                     @RequestParam Instant from, @RequestParam Instant to) {
        return rollupService.rollup(src.toUpperCase(), dst.toUpperCase(), from, to);
    }
}
