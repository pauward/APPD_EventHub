package com.appd.mvp;

import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author niwar
 *
 */
public class RateLimiter {

	static final Logger logger = LoggerFactory.getLogger(RateLimiter.class);

	protected int rateLimitTimeWindow;
	protected int rateLimitMsgCount;

	/**
	 * Current count of messages received within the rate limit time window
	 */
	protected int apiCallCounter;

	/**
	 * Fixed block time frame queue to track received API calls
	 */
	protected Queue<Long> msgTimeQueue;

	/**
	 * @param window time window for rate limit in minutes
	 * @param count  max number of messages allowed with in time window
	 */
	RateLimiter(int window, int count) {
		this.rateLimitTimeWindow = window;
		this.rateLimitMsgCount = count;
		this.apiCallCounter = 0;
		this.msgTimeQueue = new LinkedList<Long>();
	}

	/**
	 * Checks message insertion millisecond time into queue, against rate limit
	 * window in milliseconds and dequeues if expired
	 */
	synchronized private void updateRollingTimeWindow() {
		logger.debug("Rate limit reached, checking rolling window queue...");
		long currentTime = System.currentTimeMillis();
		while (msgTimeQueue.peek() != null && currentTime > (msgTimeQueue.peek() + (rateLimitTimeWindow * 60000))) {
			msgTimeQueue.poll();
			apiCallCounter--;
		}
		logger.debug("{} elements dequeued from rolling window", rateLimitMsgCount - apiCallCounter);

	}

	/**
	 * If rate limit reached returns false else adds msg receive time to queue and
	 * returns true
	 * 
	 * @return true or false
	 */
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
