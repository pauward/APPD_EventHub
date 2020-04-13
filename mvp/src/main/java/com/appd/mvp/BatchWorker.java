package com.appd.mvp;

import java.io.FileOutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.activemq.artemis.api.core.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appd.events.Event;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BatchWorker implements Runnable {

	static final Logger logger = LoggerFactory.getLogger(BatchWorker.class);

	protected int batchQueueSize;
	protected int batchCycleTime;
	protected String sinkPath;
	protected ConcurrentHashMap<String, ConcurrentLinkedQueue<Pair<Long, Event>>> eventTypeMap;

	BatchWorker(int size, int cycle, String sink,
			ConcurrentHashMap<String, ConcurrentLinkedQueue<Pair<Long, Event>>> map) {
		this.batchQueueSize = size;
		this.batchCycleTime = cycle;
		this.sinkPath = sink;
		this.eventTypeMap = map;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(1000);
				synchronized (eventTypeMap) {

					for (String eventType : eventTypeMap.keySet()) {
						ConcurrentLinkedQueue<Pair<Long, Event>> typeQueue = eventTypeMap.get(eventType);
						FileOutputStream outputStream = new FileOutputStream(sinkPath + eventType + ".txt", true);

						long currentTime = System.currentTimeMillis();
						ObjectMapper mapper = new ObjectMapper();
						StringBuilder batchedEvents = new StringBuilder();

						if (typeQueue.size() >= batchQueueSize) {
							logger.debug("Batch size reached; Pushing batch...");
							for (int countEvent = batchQueueSize; countEvent > 0; countEvent--) {
								Pair<Long, Event> val = typeQueue.poll();
								logger.debug("Writing event id {}", val.getB().getEventId());
								batchedEvents.append(mapper.writeValueAsString(val.getB()));
							}
						} else if (typeQueue.size() > 0 && typeQueue.size() < batchQueueSize
								&& currentTime >= (typeQueue.peek().getA() + (batchCycleTime * 1000))) {
							logger.debug("Event exceeded cycle time; Pushing batch...");
							while (typeQueue.peek() != null) {
								Pair<Long, Event> val = typeQueue.poll();
								logger.debug("Writing event id {}", val.getB().getEventId());
								batchedEvents.append(mapper.writeValueAsString(val.getB()));
							}
						}
						if (batchedEvents.length() > 0) {
							mapper.writeValue(outputStream, batchedEvents.toString());
							outputStream.flush();
							outputStream.close();
						}
					}

				}
			}

		} catch (InterruptedException e) {
			logger.warn("Worker Interrupted : {} ", e.getMessage());
		} catch (Exception e) {
			logger.error("Exception while writing to sink : {}", e.getMessage());
			logger.debug("Stack Trace : ", e);
		}
	}
}
