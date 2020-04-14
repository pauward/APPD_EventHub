package com.appd.mvp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.activemq.artemis.api.core.Pair;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appd.events.Event;

/**
 * @author niwar
 *
 */
public class BatchBlockTests {

	static final Logger logger = LoggerFactory.getLogger(BatchBlockTests.class);
	static final String path = "src/test/resources/";

	@Test
	void testMessageQueue() throws InterruptedException {
		logger.debug("@TEST : Check Event Submission");

		BatchProcessor batchEngine = new BatchProcessor(50, 15, 5, path);

		ArrayList<Event> eventList = new ArrayList<Event>();
		eventList.add(new Event("1", "EType1", null));
		eventList.add(new Event("2", "EType2", null));
		eventList.add(new Event("3", "EType2", null));

		assertTrue(batchEngine.submitEvents(eventList));
		assertTrue(batchEngine.eventTypeMap.keySet().size() == 2);
		assertTrue(batchEngine.eventTypeMap.get("EType1").size() == 1);
		assertTrue(batchEngine.eventTypeMap.get("EType2").size() == 2);

		Thread[] brokers = batchEngine.workers;
		for (int id = 0; id < brokers.length; id++) {
			brokers[id].interrupt();
		}
		Thread.sleep(1000); // Wait to interrupt all created workers, before next test
		logger.debug("@TEST : Successful test");
	}

	@Test
	void testWorker() throws InterruptedException {
		logger.debug("@TEST : Check a Worker Dequeue");

		ConcurrentHashMap<String, LinkedBlockingQueue<Pair<Long, Event>>> eventTypeMap = new ConcurrentHashMap<String, LinkedBlockingQueue<Pair<Long, Event>>>();

		fillEvents(eventTypeMap, "EType1", 7);
		fillEvents(eventTypeMap, "EType2", 7);

		Thread worker = new Thread(new BatchWorker(5, 2, path, eventTypeMap));
		worker.start();
		Thread worker2 = new Thread(new BatchWorker(5, 2, path, eventTypeMap));
		worker2.start();

		worker.join(4000);
		worker2.join(4000);

		// All messages must be processed
		assertTrue(eventTypeMap.get("EType1").size() == 0);
		assertTrue(eventTypeMap.get("EType2").size() == 0);

		logger.debug("@TEST : Successful test");
	}

	/**
	 * @param eventTypeMap Concurrent map
	 * @param type         Type of events to populate
	 * @param n            Number of event elements to add
	 */
	private void fillEvents(ConcurrentHashMap<String, LinkedBlockingQueue<Pair<Long, Event>>> eventTypeMap, String type,
			int n) {
		LinkedBlockingQueue<Pair<Long, Event>> eventTypeQueue = new LinkedBlockingQueue<Pair<Long, Event>>();
		for (int i = 0; i < n; i++)
			eventTypeQueue
					.add(new Pair<Long, Event>(System.currentTimeMillis(), new Event(String.valueOf(n), type, null)));
		eventTypeMap.put(type, eventTypeQueue);
	}

}
