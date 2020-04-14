package com.appd.iop;

import java.util.LinkedList;

import com.appd.events.Event;

/**
 * Output format to sink/topic
 * 
 * @author niwar
 *
 */
public class OutToSink {

	private String batchId;
	private LinkedList<Event> eventList;

	public OutToSink(String uuid, LinkedList<Event> list) {
		this.batchId = uuid;
		this.eventList = list;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public LinkedList<Event> getEventList() {
		return eventList;
	}

	public void setEventList(LinkedList<Event> eventList) {
		this.eventList = eventList;
	}

}
