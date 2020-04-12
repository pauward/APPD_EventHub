package com.appd.mvp;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appd.events.Event;

public class BatchProcessor {
	
	static final Logger logger = LoggerFactory.getLogger(BatchProcessor.class);
	protected int batchQueueSize;
	protected int batchCycleTime;
	protected BlockingQueue<String> syncQueue;
	
	BatchProcessor(int size, int cycle) {
		this.batchQueueSize = size;
		this.batchCycleTime = cycle;
		this.syncQueue = new SynchronousQueue<>(true);
	}

	public boolean submitEvents(ArrayList<Event> eventList) {
		try {
			for(Event e : eventList) {
			//	e.getType()
			}
		}catch(Exception e) {
			
			return false;
		}
		return true;
	}

}
