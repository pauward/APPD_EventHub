package com.appd.mvp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ConfigProperties.class)
public class MvpApplication {
    static final Logger logger = LoggerFactory.getLogger(MvpApplication.class);
	public static void main(String[] args) {
		if(args!=null && args.length > 0)
			logger.info("Starting appplication {}", args[0]);
		SpringApplication.run(MvpApplication.class, args);
		
	}
	
}