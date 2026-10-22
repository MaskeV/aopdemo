package com.netraa.aopdemo.document;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import com.netraa.aopdemo.entity.Service;

@Document(indexName = "logspermin")
public class LogsPerMin {
	
	@Id
	private String id;
  
    private Instant starttimestamp;
    private String endtimestamp;
    private Service service;
    private Double avgresponseTimeMs;
    private int reqCount;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Instant getStarttimestamp() {
		return starttimestamp;
	}
	public void setStarttimestamp(Instant starttimestamp) {
		this.starttimestamp = starttimestamp;
	}
	public String getEndtimestamp() {
		return endtimestamp;
	}
	public void setEndtimestamp(String endtimestamp) {
		this.endtimestamp = endtimestamp;
	}
	public Service getService() {
		return service;
	}
	public void setService(Service service) {
		this.service = service;
	}
	public Double getAvgresponseTimeMs() {
		return avgresponseTimeMs;
	}
	public void setAvgresponseTimeMs(Double avgresponseTimeMs) {
		this.avgresponseTimeMs = avgresponseTimeMs;
	}
	public int getReqCount() {
		return reqCount;
	}
	public void setReqCount(int reqCount) {
		this.reqCount = reqCount;
	}
    
    

}
