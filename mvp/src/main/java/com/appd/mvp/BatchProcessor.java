package com.appd.mvp;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.activemq.artemis.api.core.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appd.events.Event;

public class BatchProcessor {

	static final Logger logger = LoggerFactory.getLogger(BatchProcessor.class);
	
	protected int batchQueueSize;
	protected int batchCycleTime;
	protected int batchWorkers;
	
	protected String sinkPath;
	protected Thread[] workers;
	
	protected ConcurrentHashMap<String, ConcurrentLinkedQueue<Pair<Long, Event>>> eventTypeMap;
	

	BatchProcessor(int size, int cycle, int processors, String sink) {
		this.batchQueueSize = size;
		this.batchCycleTime = cycle;
		this.batchWorkers = processors;
		this.sinkPath = sink;
		
		this.eventTypeMap = new ConcurrentHashMap<String, ConcurrentLinkedQueue<Pair<Long, Event>>>();
		
		this.workers = new Thread[processors];
		for (int i = 0; i < processors; i++) {
			Thread worker = new Thread(new BatchWorker(size, cycle, sink, this.eventTypeMap));
			worker.start();
			this.workers[i] = worker;
		}
	}

	synchronized public boolean submitEvents(ArrayList<Event> eventList) {
		try {
			for (Event e : eventList) {
				ConcurrentLinkedQueue<Pair<Long, Event>> eventTypeQueue;

				if (eventTypeMap.contains(e.getType()))
					eventTypeQueue = eventTypeMap.get(e.getType());
				else
					eventTypeQueue = new ConcurrentLinkedQueue<Pair<Long, Event>>();

				eventTypeQueue.add(new Pair<Long, Event>(System.currentTimeMillis(), e));
				eventTypeMap.put(e.getType(), eventTypeQueue);
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
