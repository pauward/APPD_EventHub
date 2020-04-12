package com.appd.mvp;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appd.events.Event;

public class BatchProcessor {
	
	static final Logger logger = LoggerFactory.getLogger(BatchProcessor.class);
	protected int batchQueueSize;
	protected int batchCycleTime;

	BatchProcessor(int size, int cycle) {
		this.batchQueueSize = size;
		this.batchCycleTime = cycle;
	}

	public boolean submitEvents(ArrayList<Event> eventList) {
		// TODO Auto-generated method stub
		return true;
	}

}
