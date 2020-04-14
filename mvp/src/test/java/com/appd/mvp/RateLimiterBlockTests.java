package com.appd.mvp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author niwar
 *
 */
class RateLimiterBlockTests {
	static final Logger logger = LoggerFactory.getLogger(RateLimiterBlockTests.class);

	@Test
	void testInit() {
		logger.debug("@TEST : Check Bean Initialization");

		RateLimiter limiter = new RateLimiter(90, 100000);
		assertTrue(limiter.rateLimitMsgCount == 100000);
		assertTrue(limiter.rateLimitTimeWindow == 90);
		assertTrue(limiter.apiCallCounter == 0);

		logger.debug("@TEST : Successful test");
	}

	@Test
	void testLimiter() {
		logger.debug("@TEST : Limiter test");
		RateLimiter limiter = new RateLimiter(1, 1);
		logger.debug("@TEST : Sending first call");
		assertTrue(limiter.isAllowed() == true);
		logger.debug("@TEST : Sending second call");
		assertTrue(limiter.isAllowed() == false);
		logger.debug("@TEST : Successful test; Limiter blocked API after limit");
		logger.debug("@TEST : End of test");
	}

	@Test
	void testLimiterVariables() {
		logger.debug("@TEST : Limiter varialbe test");
		RateLimiter limiter = new RateLimiter(1, 2);
		int i = 1;
		while (i <= 5) {
			logger.debug("@TEST : Sending call {}", i);
			limiter.isAllowed();
			i++;
		}
		assertTrue(limiter.apiCallCounter == 2);
		assertTrue(limiter.msgTimeQueue.size() == 2);
		logger.debug("@TEST : Successful test; Limiter queue maxed at specified limit; did not dequeue");
		logger.debug("@TEST : End of test");
	}

	@Test
	void testLimiterTimer() throws InterruptedException {
		logger.debug("@TEST : Limiter timer test");
		// Set time window to zero
		RateLimiter limiter = new RateLimiter(0, 2);
		int i = 1;
		while (i <= 5) {
			logger.debug("@TEST : Sending call {}", i);
			limiter.isAllowed();
			Thread.sleep(10);
			i++;
		}
		assertTrue(limiter.apiCallCounter == 1);
		assertTrue(limiter.msgTimeQueue.size() == 1);
		logger.debug("@TEST : Successful test; Dequeued expired calls successfully");
		logger.debug("@TEST : End of test");
	}
}
