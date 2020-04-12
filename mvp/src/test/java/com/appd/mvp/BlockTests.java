package com.appd.mvp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.appd.events.Event;

class BlockTests {

	@Test
	void test() {
		RateLimiter limiter = new RateLimiter();
		assertTrue(limiter.isAllowed(new Event()));
	}

}
