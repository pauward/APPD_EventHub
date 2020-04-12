package com.appd.events;

public class Event {

	private String eventId;
	private String type;
	private EventBody BodyObject;
	
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public EventBody getBodyObject() {
		return BodyObject;
	}
	public void setBodyObject(EventBody bodyObject) {
		BodyObject = bodyObject;
	}
}


