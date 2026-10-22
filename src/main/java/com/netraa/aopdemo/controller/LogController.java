package com.netraa.aopdemo.controller;

import com.netraa.aopdemo.dto.LogReqDto;
import com.netraa.aopdemo.entity.Log;
import com.netraa.aopdemo.service.LogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping()
    public ResponseEntity<String> saveLog(@RequestBody LogReqDto logReqDto){
        logService.saveLog(logReqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Log saved successfully");

    }

    @PostMapping("/addAll")
    public ResponseEntity<String> saveLog(@RequestBody List<LogReqDto> logs){
        logService.saveLogs(logs);
        return ResponseEntity.status(HttpStatus.CREATED).body("Log saved successfully");

    }

    @GetMapping
    public ResponseEntity<List<Log>> getLogs(){
        List<Log> logs = logService.getLogs();
        return ResponseEntity.ok(logs);
    }


}
