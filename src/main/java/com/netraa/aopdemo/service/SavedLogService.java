package com.netraa.aopdemo.service;

import com.netraa.aopdemo.entity.SavedLog;
import com.netraa.aopdemo.repository.LogRepository;
import org.springframework.stereotype.Service;

@Service
public class SavedLogService {

    private final LogRepository logRepository;

    public SavedLogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void saveLogAggregatePerMin(SavedLog savedLog){
        logRepository.save(savedLog);
    }


}
