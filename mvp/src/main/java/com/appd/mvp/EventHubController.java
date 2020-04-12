package com.appd.mvp;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appd.events.EventMessageList;
import com.fasterxml.uuid.Generators;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
public class EventHubController {

	@Autowired
	ConfigProperties defaultConfig;

	static final Logger logger = LoggerFactory.getLogger(EventHubController.class);
	protected final RateLimiter limiter;
	protected final BatchProcessor batchEngine;

	@Autowired
	public EventHubController(RateLimiter rateLimiter, BatchProcessor batchProcessor) {
		limiter = rateLimiter;
		batchEngine = batchProcessor;
	}

	@PostMapping("/event-gateway")
	@ResponseBody
	public ResponseEntity<GatewayResponse> crazyEventGateway(
			@RequestBody(required = true) @Valid EventMessageList msgList,
			@RequestHeader(value = "Authorization", required = true) @Valid String authorization,
			@RequestHeader(value = "Tenant", required = true) @Valid String tenant) {

		try {
			if (!limiter.isAllowed()) {
				logger.warn("Exceeded rate limit; Rejecting request.");
				return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
			}

			if (authorization.equals(defaultConfig.getDefaultToken())
					&& tenant.equals(defaultConfig.getDefaultTenant())) {
				logger.debug("Client validated, proceeding to process events...");

				if (msgList.getEventList().isEmpty()) {
					logger.error("Empty set of events received");
					return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
				}

				logger.debug("Submitting {} events to the batch processor", msgList.getEventList().size());
				if (batchEngine.submitEvents(msgList.getEventList()) == false) {
					logger.error("Submission to batch failed");
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
				}

				String uuid = Generators.timeBasedGenerator().generate().toString();
				logger.info("Event submit sucesss; Job reference UUID : {}", uuid);
				logger.warn("Message accepeted; Current call counter value - {}", limiter.apiCallCounter);
				logger.warn("queue size {}, element {} ", limiter.msgTimeQueue.size(),limiter.msgTimeQueue.peek());
				GatewayResponse responseBody = new GatewayResponse(uuid);
				return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);

			} else {
				logger.warn("Unauthorized access");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

		} catch (Exception e) {
			logger.error("Exception while processing client request : {}", e.getMessage());
			logger.debug("Stack Trace : ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}