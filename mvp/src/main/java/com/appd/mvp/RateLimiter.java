package com.appd.mvp;

import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RateLimiter {

	static final Logger logger = LoggerFactory.getLogger(RateLimiter.class);

	protected int rateLimitTimeWindow;
	protected int rateLimitMsgCount;
	protected int apiCallCounter;

	// Fixed block time frame to track received API calls
	protected Queue<Long> msgTimeQueue;

	RateLimiter(int window, int count) {
		this.rateLimitTimeWindow = window; // Unit : Minutes
		this.rateLimitMsgCount = count;
		this.apiCallCounter = 0;
		this.msgTimeQueue = new LinkedList<Long>(); // For better insertion and delete performance
	}

	synchronized private void updateRollingTimeWindow() {
		logger.debug("Rate limit reached, checking rolling window queue...");
		long currentTime = System.currentTimeMillis();
		while (msgTimeQueue.peek() != null && currentTime > (msgTimeQueue.peek() + (rateLimitTimeWindow * 60000))) {
			msgTimeQueue.poll();
			apiCallCounter--;
		}
		logger.debug("{} elements dequeued from rolling window", rateLimitMsgCount - apiCallCounter);

	}

	synchronized boolean isAllowed() {
		if (apiCallCounter == rateLimitMsgCount)
			updateRollingTimeWindow();
		if (apiCallCounter < rateLimitMsgCount) {
			msgTimeQueue.add(System.currentTimeMillis());
			apiCallCounter++;
			return true;
		}
		return false;
	}
}
