package com.appd.mvp;

import java.io.FileOutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.activemq.artemis.api.core.Pair;

import com.appd.events.Event;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BatchWorker implements Runnable {

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
				synchronized (eventTypeMap) {

					for (String eventType : eventTypeMap.keySet()) {

						ConcurrentLinkedQueue<Pair<Long, Event>> typeQueue = eventTypeMap.get(eventType);
						FileOutputStream outputStream = new FileOutputStream(sinkPath + eventType + ".txt", true);

						long currentTime = System.currentTimeMillis();
						ObjectMapper mapper = new ObjectMapper();
						
						if (typeQueue.size() >= batchQueueSize) {

							for (int countEvent = batchQueueSize; countEvent > 0; countEvent--)
								mapper.writeValue(outputStream, typeQueue.poll().getB());

						} else if (typeQueue.size() > 0 && typeQueue.size() < batchQueueSize
								&& currentTime >= (typeQueue.peek().getA() + (batchCycleTime * 60000))) {

							while (typeQueue.peek() != null)
								mapper.writeValue(outputStream, typeQueue.poll().getB());

						}
					}

				}
			}

		} catch (Exception e) {

		}
	}
}
