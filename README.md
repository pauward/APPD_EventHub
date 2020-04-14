# APPD_EventHub
REST API for event ingestion and management 

API Definition : 

POST /event-gateway
Post an event list to the gateway (eventGatewayPost)

Consumes
This API call consumes the following media types via the Content-Type request header:
	application/json

	Request body
		body Events (required)
		Parameter — List of events to ingest
		
		Example data
		Authorization : <Insert token here>
		Tenant  : us-west-sr
		Content-Type: application/json
		{
			"eventList": [{
				"eventId": "3c6435c8-7cd5-11ea-8d95-43f4ee316fe5",
				"type": "EType1",
				"body": {
					"name": "appd_mp_event",
					"eventStart": 1586622559298,
					"severity": 2
				}
			}]
		}

	Return type
		Job
		
		Example data
		Response-Code : 202
		Content-Type: application/json
		{
			"uuid" : "046b6c7f-0b8a-43b9-b35d-6489e6daee91"
		}

Produces
This API call produces the following media types according to the Accept request header; the media type will be conveyed by the Content-Type response header.
	application/json
	
	Responses
		202
		Successful job submission - Accepted Job
		401
		User not allowed - Unauthorized
		422
		Empty events list - Unprocessable entity
		429
		Rate limit rejection - Too many requests
		500
		Exception while processing request - Internal server error

Models
	Events
		eventList (required)
		array[Event]

	Event
		eventId (optional)
		UUID format: uuid

		type (required)
		String format: text
		example: ["EType1","EType2","EType3","EType4"]

		body (optional)
		EventDetails
		
	EventDetails
		name (optional)
		String format: text
		
		eventStart (optional)
		String format: timestamp
		
		severity (optional)
		Integer format: int32

	Job
		uuid (optional)
		UUID format: uuid

