package com.appd.mvp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BlockTests {
	static final Logger logger = LoggerFactory.getLogger(BlockTests.class);

	@Test
	void testInit() {
		RateLimiter limiter = new RateLimiter(1, 1);
		assertTrue(limiter.rateLimitMsgCount == 1);
		assertTrue(limiter.rateLimitTimeWindow == 1);
		assertTrue(limiter.apiCallCounter == 0);
	}

	@Test
	void testLimiter() {
		logger.debug("@TEST : Limiter test");
		RateLimiter limiter = new RateLimiter(1, 1);
		logger.debug("Sending first call");
		assertTrue(limiter.isAllowed() == true);
		logger.debug("Sending second call");
		assertTrue(limiter.isAllowed() == false);
		logger.debug("Successful test; Limiter blocked API after limit");
		logger.debug("##### end of test #####");
	}

	@Test
	void testLimiterVariables() {
		logger.debug("@TEST : Limiter varialbe test");
		RateLimiter limiter = new RateLimiter(1, 2);
		int i = 1;
		while (i <= 5) {
			logger.debug("Sending call {}", i);
			limiter.isAllowed();
			i++;
		}
		assertTrue(limiter.apiCallCounter == 2);
		assertTrue(limiter.msgTimeQueue.size() == 2);
		logger.debug("Successful test; Limiter queue maxed at specified limit; did not dequeue");
		logger.debug("##### end of test #####");
	}

	@Test
	void testLimiterTimer() throws InterruptedException {
		logger.debug("@TEST : Limiter timer test");
		RateLimiter limiter = new RateLimiter(0, 2);
		int i = 1;
		while (i <= 5) {
			logger.debug("Sending call {}", i);
			limiter.isAllowed();
			Thread.sleep(10);
			i++;
		}
		assertTrue(limiter.apiCallCounter == 1);
		assertTrue(limiter.msgTimeQueue.size() == 1);
		logger.debug("Successful test; Dequeued expired calls successfully");
		logger.debug("##### end of test #####");
	}
}
