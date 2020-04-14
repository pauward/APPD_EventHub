package com.appd.mvp;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.activemq.artemis.api.core.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appd.events.Event;

/**
 * @author niwar
 * 
 */
public class BatchProcessor {

	static final Logger logger = LoggerFactory.getLogger(BatchProcessor.class);

	protected int batchQueueSize;
	protected int batchCycleTime;
	protected int batchWorkers;

	protected String sinkPath;
	protected Thread[] workers;

	/**
	 * Thread safe map to hold independent blocking queues for each event type
	 */
	protected ConcurrentHashMap<String, LinkedBlockingQueue<Pair<Long, Event>>> eventTypeMap;

	/**
	 * @param size       Batch queue size
	 * @param cycle      Max time msg must spend in service
	 * @param processors Number of parallel worker threads
	 * @param sink       Output directory path
	 */
	BatchProcessor(int size, int cycle, int processors, String sink) {

		this.batchQueueSize = size;
		this.batchCycleTime = cycle;
		this.batchWorkers = processors;
		this.sinkPath = sink;

		this.eventTypeMap = new ConcurrentHashMap<String, LinkedBlockingQueue<Pair<Long, Event>>>();

		this.workers = new Thread[processors];
		for (int id = 0; id < processors; id++) {
			Thread worker = new Thread(new BatchWorker(size, cycle, sink, this.eventTypeMap));
			worker.start();
			this.workers[id] = worker;
		}
		logger.debug("Created {} workers", workers.length);
	}

	/**
	 * Parse events in JSON and submit to appropriate queue based on event type
	 * 
	 * @param eventList List of events in JSON pay load
	 * @return true or false
	 */
	synchronized public boolean submitEvents(ArrayList<Event> eventList) {
		try {
			for (Event e : eventList) {
				LinkedBlockingQueue<Pair<Long, Event>> eventTypeQueue;

				if (eventTypeMap.containsKey(e.getType())) {
					eventTypeQueue = eventTypeMap.get(e.getType());
					logger.debug("Event type queue - {} available with {} events", e.getType(), eventTypeQueue.size());
				} else {
					logger.debug("Creating new event type queue {} ", e.getType());
					eventTypeQueue = new LinkedBlockingQueue<Pair<Long, Event>>();
					eventTypeMap.put(e.getType(), eventTypeQueue);
				}
				eventTypeQueue.add(new Pair<Long, Event>(System.currentTimeMillis(), e));

			}
		} catch (Exception e) {
			logger.error("Exception while submitting events to queue : {}", e.getMessage());
			logger.debug("Stack Trace : ", e);
			return false;
		}
		return true;
	}

}
