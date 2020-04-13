package com.appd.mvp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(ConfigProperties.class)
public class MvpApplication {
    static final Logger logger = LoggerFactory.getLogger(MvpApplication.class);
    
    @Autowired
	ConfigProperties defaultConfig;
    
    @Bean
    public RateLimiter getRateLimiter() {
        return new RateLimiter(defaultConfig.getRateLimitTimeWindow(), defaultConfig.getRateLimitMsgCount());
    }
    
    @Bean
    public BatchProcessor getBatchProcessor() {
        return new BatchProcessor(defaultConfig.getBatchQueueSize(), defaultConfig.getBatchCycleTime(), defaultConfig.getBatchWorkers(), defaultConfig.getSinkPath());
    }
    
	public static void main(String[] args) {
		if(args!=null && args.length > 0)
			logger.info("Starting appplication {}", args[0]);
		SpringApplication.run(MvpApplication.class, args);
		
	}
	
}