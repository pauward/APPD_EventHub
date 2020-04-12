package com.appd.mvp;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.uuid.Generators;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class EventHub {

    @Autowired
    ConfigProperties defaultConfig;
    
    static final Logger logger = LoggerFactory.getLogger(EventHub.class);

	@PostMapping("/event-gateway")
	public String crazyEventGateway(@RequestHeader(value = "Authorization") String authorization,@RequestHeader(value = "Tenant") String tenant) {
		
		logger.debug("Received request from a client");

		if(authorization.equals(defaultConfig.getDefaultToken()) && tenant.equals(defaultConfig.getDefaultTenant())) {
			UUID uuid = Generators.timeBasedGenerator().generate();
			return uuid.toString();
		}
		return "403:Unauthorized";

	}
    
}