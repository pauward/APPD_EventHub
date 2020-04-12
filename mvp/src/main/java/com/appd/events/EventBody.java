package com.appd.events;

import java.util.ArrayList;

public class EventBody {

	private String name;
	private float eventStart;
	private EventData EventDataObject;
	private float severity;
	private ArrayList<EventTag> tags = new ArrayList<EventTag>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getEventStart() {
		return eventStart;
	}

	public void setEventStart(float eventStart) {
		this.eventStart = eventStart;
	}

	public EventData getEventDataObject() {
		return EventDataObject;
	}

	public void setEventDataObject(EventData eventDataObject) {
		EventDataObject = eventDataObject;
	}

	public float getSeverity() {
		return severity;
	}

	public void setSeverity(float severity) {
		this.severity = severity;
	}

	public ArrayList<EventTag> getTags() {
		return tags;
	}

	public void setTags(ArrayList<EventTag> tags) {
		this.tags = tags;
	}
}
