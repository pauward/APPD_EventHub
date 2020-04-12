package com.appd.events;

import java.util.ArrayList;

public class EventMessageList {
	
	private ArrayList<Event> eventList = new ArrayList<Event>();
	
	public EventMessageList(){}
	
	public ArrayList<Event> getEventList() {
		return eventList;
	}
	public void setEventList(ArrayList<Event> eventList) {
		this.eventList = eventList;
	}
	
}
