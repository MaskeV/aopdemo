package com.netraa.aopdemo.repository;

import com.netraa.aopdemo.entity.SavedLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface LogRepository extends ElasticsearchRepository<SavedLog, String>{


}
