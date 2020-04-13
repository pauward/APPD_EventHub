package com.appd.mvp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.activemq.artemis.api.core.Pair;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appd.events.Event;

public class BatchBlockTests {

	static final Logger logger = LoggerFactory.getLogger(BatchBlockTests.class);


	@Test
	void testMessageQueue() {
		logger.debug("@TEST : Check Event Submission");
		String path = "src/test/resources/";
		BatchProcessor batchEngine = new BatchProcessor(50, 15, 5, path);

		ArrayList<Event> eventList = new ArrayList<Event>();
		eventList.add(new Event("1", "EType1", null));
		eventList.add(new Event("2", "EType2", null));
		eventList.add(new Event("3", "EType2", null));

		batchEngine.submitEvents(eventList);

		assertTrue(batchEngine.eventTypeMap.keySet().size() == 2);
		assertTrue(batchEngine.eventTypeMap.get("EType1").size() == 1);
		assertTrue(batchEngine.eventTypeMap.get("EType2").size() == 2);
		
		Thread[] brokers = batchEngine.workers;
		for (int id = 0; id < brokers.length; id++) {
			brokers[id].interrupt();
		}
		
		logger.debug("@TEST : Successful test");
	}
	
	@Test
	void testWorker() throws InterruptedException {
		logger.debug("@TEST : Check a Worker Dequeue");
		String path = "src/test/resources/";

		ConcurrentHashMap<String, ConcurrentLinkedQueue<Pair<Long, Event>>> eventTypeMap = new ConcurrentHashMap<String, ConcurrentLinkedQueue<Pair<Long, Event>>>();
		ConcurrentLinkedQueue<Pair<Long, Event>> eventTypeQueue = new ConcurrentLinkedQueue<Pair<Long, Event>>();
		eventTypeQueue.add(new Pair<Long, Event>(System.currentTimeMillis(), new Event("1", "EType1", null)));
		eventTypeQueue.add(new Pair<Long, Event>(System.currentTimeMillis(), new Event("2", "EType1", null)));
		eventTypeQueue.add(new Pair<Long, Event>(System.currentTimeMillis(), new Event("3", "EType1", null)));
		eventTypeQueue.add(new Pair<Long, Event>(System.currentTimeMillis(), new Event("4", "EType1", null)));
		eventTypeQueue.add(new Pair<Long, Event>(System.currentTimeMillis(), new Event("5", "EType1", null)));
		eventTypeQueue.add(new Pair<Long, Event>(System.currentTimeMillis(), new Event("6", "EType1", null)));
		eventTypeQueue.add(new Pair<Long, Event>(System.currentTimeMillis(), new Event("7", "EType1", null)));
		eventTypeMap.put("EType1", eventTypeQueue);
		Thread worker = new Thread(new BatchWorker(5, 2, path, eventTypeMap));
		worker.start();
		worker.join(4000);
		assertTrue(eventTypeMap.get("EType1").size()==0);
		logger.debug("@TEST : Successful test");
	}
	

	
}
