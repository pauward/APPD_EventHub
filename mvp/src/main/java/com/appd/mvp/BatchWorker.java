package com.appd.mvp;

import java.io.FileOutputStream;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.activemq.artemis.api.core.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appd.events.Event;
import com.appd.iop.OutToSink;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;

public class BatchWorker implements Runnable {

	static final Logger logger = LoggerFactory.getLogger(BatchWorker.class);

	protected int batchQueueSize;
	protected int batchCycleTime;
	protected String sinkPath;

	protected ConcurrentHashMap<String, LinkedBlockingQueue<Pair<Long, Event>>> eventTypeMap;

	BatchWorker(int size, int cycle, String sink,
			ConcurrentHashMap<String, LinkedBlockingQueue<Pair<Long, Event>>> map) {
		this.batchQueueSize = size;
		this.batchCycleTime = cycle;
		this.sinkPath = sink;
		this.eventTypeMap = map;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(1);
				ObjectMapper mapper = new ObjectMapper();

				for (String eventType : eventTypeMap.keySet()) {

					long currentTime = System.currentTimeMillis();
					LinkedList<Event> list = new LinkedList<Event>();
					LinkedBlockingQueue<Pair<Long, Event>> typeQueue = eventTypeMap.get(eventType);
					synchronized (typeQueue) {

						if (typeQueue.size() >= batchQueueSize) {
							logger.debug("Batch size reached for {}; Pushing batch...", eventType);
							for (int countEvent = batchQueueSize; countEvent > 0; countEvent--) {
								if (typeQueue.peek() != null)
									list.add(typeQueue.poll().getB());
							}
						} else if (typeQueue.size() > 0 && typeQueue.size() < batchQueueSize
								&& currentTime >= (typeQueue.peek().getA() + (batchCycleTime * 1000))) {
							logger.debug("Event exceeded cycle time in {} queue; Pushing batch...", eventType);
							while (typeQueue.peek() != null)
								list.add(typeQueue.poll().getB());

						}
						if (list.size() > 0) {
							String uuid = Generators.timeBasedGenerator().generate().toString();
							logger.debug("Writing batch {} for type {} with {} events", uuid, eventType, list.size());
							FileOutputStream outputStream = new FileOutputStream(sinkPath + eventType + ".txt", true);
							outputStream.write(mapper.writeValueAsString(new OutToSink(uuid, list)).getBytes());
							outputStream.write(new String("\n").getBytes());
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
