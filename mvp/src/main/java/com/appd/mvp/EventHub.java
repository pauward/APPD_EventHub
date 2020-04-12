package com.appd.mvp;

import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appd.events.EventMessageList;
import com.fasterxml.uuid.Generators;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
public class EventHub {

	@Autowired
	ConfigProperties defaultConfig;

	static final Logger logger = LoggerFactory.getLogger(EventHub.class);

	@PostMapping("/event-gateway")
	@ResponseBody
	public ResponseEntity<GatewayResponse> crazyEventGateway(
			@RequestBody(required = true) @Valid EventMessageList events,
			@RequestHeader(value = "Authorization") String authorization,
			@RequestHeader(value = "Tenant") String tenant) {

		if (authorization.equals(defaultConfig.getDefaultToken()) && tenant.equals(defaultConfig.getDefaultTenant())) {
			UUID uuid = Generators.timeBasedGenerator().generate();
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(new GatewayResponse(uuid.toString()));

		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}

}