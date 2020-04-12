package com.appd.mvp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest
@AutoConfigureMockMvc
class MvpApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Value("${mvp.defaultToken}")
	private String defaultToken;
	@Value("${mvp.defaultTenant}")
	private String defaultTenant;
	@Value("${mvp.rateLimitTimeWindow}")
	private int rateLimitTimeWindow;
	@Value("${mvp.rateLimitMsgCount}")
	private int rateLimitMsgCount;
	@Value("${mvp.batchQueueSize}")
	private int batchQueueSize;
	@Value("${mvp.batchCycleTime}")
	private int batchCycleTime;

	@Test
	public void checkPropertyLoading() {
		assertThat(defaultToken).isEqualTo("S8EOKCffRvEYTO1pIXIo7Q");
		assertThat(defaultTenant).isEqualTo("us-west-sr");
		assertThat(rateLimitTimeWindow).isEqualTo(60);
		assertThat(rateLimitMsgCount).isEqualTo(50000);
		assertThat(batchQueueSize).isEqualTo(1000);
		assertThat(batchCycleTime).isEqualTo(15);

	}

	@Test
	public void sendGoodEvents() throws Exception {
		MockHttpServletRequestBuilder restApiCall = MockMvcRequestBuilders.post("/event-gateway");
		restApiCall.header("Authorization", defaultToken);
		restApiCall.header("Tenant", defaultTenant);
		restApiCall.accept(MediaType.APPLICATION_JSON);
		restApiCall.contentType(MediaType.APPLICATION_JSON);
		restApiCall.content(getTestEvents());
		ResultActions result = mvc.perform(restApiCall);
		result.andExpect(status().isAccepted()).andExpect(content().string(containsString("uuid")));

	}

	@Test
	public void sendUnAuthorizedEvent() throws Exception {
		MockHttpServletRequestBuilder restApiCall = MockMvcRequestBuilders.post("/event-gateway");
		restApiCall.header("Authorization", "I_DONT_HAVE_ACCESS");
		restApiCall.header("Tenant", defaultTenant);
		restApiCall.accept(MediaType.APPLICATION_JSON);
		restApiCall.contentType(MediaType.APPLICATION_JSON);
		restApiCall.content(getTestEvents());
		ResultActions result = mvc.perform(restApiCall);
		result.andExpect(status().isUnauthorized());

	}
	
	@Test
	public void sendNoEvent() throws Exception {
		MockHttpServletRequestBuilder restApiCall = MockMvcRequestBuilders.post("/event-gateway");
		restApiCall.header("Authorization", "I_DONT_HAVE_ACCESS");
		restApiCall.header("Tenant", defaultTenant);
		restApiCall.accept(MediaType.APPLICATION_JSON);
		restApiCall.contentType(MediaType.APPLICATION_JSON);
		restApiCall.content("");
		ResultActions result = mvc.perform(restApiCall);
		result.andExpect(status().isBadRequest());

	}

	private String getTestEvents() throws FileNotFoundException, IOException {
		File eventsJson = new File("src/test/resources/events.json");
		StringBuilder jsonString = new StringBuilder();
		BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(eventsJson)));
		String line;
		while ((line = buffer.readLine()) != null) {
			jsonString.append(line + System.lineSeparator());
		}
		buffer.close();
		return jsonString.toString();
	}

}
