package com.appd.mvp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

@Primary
@Configuration
@ConfigurationProperties(prefix = "mvp")
@PropertySource(value = "classpath:configuration.properties")
public class ConfigProperties {
	
	private String defaultToken;
	private String defaultTenant;
	private String sinkPath;
	private int rateLimitTimeWindow;
	private int rateLimitMsgCount;
	private int batchQueueSize;
	private int batchCycleTime;
	private int batchWorkers;
	
	public String getDefaultToken() {
		return defaultToken;
	}

	public void setDefaultToken(String defaultToken) {
		this.defaultToken = defaultToken;
	}

	public String getDefaultTenant() {
		return defaultTenant;
	}

	public void setDefaultTenant(String defaultTenant) {
		this.defaultTenant = defaultTenant;
	}

	public int getRateLimitTimeWindow() {
		return rateLimitTimeWindow;
	}

	public void setRateLimitTimeWindow(int rateLimitTimeWindow) {
		this.rateLimitTimeWindow = rateLimitTimeWindow;
	}

	public int getRateLimitMsgCount() {
		return rateLimitMsgCount;
	}

	public void setRateLimitMsgCount(int rateLimitMsgCount) {
		this.rateLimitMsgCount = rateLimitMsgCount;
	}

	public int getBatchQueueSize() {
		return batchQueueSize;
	}

	public void setBatchQueueSize(int batchQueueSize) {
		this.batchQueueSize = batchQueueSize;
	}

	public int getBatchCycleTime() {
		return batchCycleTime;
	}

	public void setBatchCycleTime(int batchCycleTime) {
		this.batchCycleTime = batchCycleTime;
	}

	public int getBatchWorkers() {
		return batchWorkers;
	}

	public void setBatchWorkers(int batchWorkers) {
		this.batchWorkers = batchWorkers;
	}

	public String getSinkPath() {
		return sinkPath;
	}

	public void setSinkPath(String sinkPath) {
		this.sinkPath = sinkPath;
	}


}
