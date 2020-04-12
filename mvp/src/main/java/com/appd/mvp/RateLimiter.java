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

	// Fixed block time frame to track number of received API calls
	protected Queue<Long> msgTimeQueue;

	RateLimiter(int window, int count) {
		this.rateLimitTimeWindow = window;
		this.rateLimitMsgCount = count;
		this.apiCallCounter = 0;
		this.msgTimeQueue = new LinkedList<Long>(); // For better insertion and delete performance
	}

	private void updateRollingTimeWindow() {
		if (apiCallCounter == rateLimitMsgCount) {
			logger.debug("Rate limit reached, checking rolling window queue...");
			long currentTime = System.currentTimeMillis();
			while (msgTimeQueue.peek() != null && currentTime > (msgTimeQueue.peek() + (rateLimitTimeWindow * 60000))) {
				if (msgTimeQueue.poll() == null)
					break;
				apiCallCounter--;
			}
			logger.debug("{} elements dequeued from rolling window", rateLimitMsgCount - apiCallCounter);
		}
	}

	boolean isAllowed() {
		updateRollingTimeWindow();
		if (apiCallCounter < rateLimitMsgCount) {
			msgTimeQueue.add(System.currentTimeMillis());
			apiCallCounter++;
			return true;
		}
		return false;
	}
}
