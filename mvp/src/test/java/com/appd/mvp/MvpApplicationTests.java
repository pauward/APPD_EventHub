package com.appd.mvp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
	public void sendEvent() throws Exception {
		
		MockHttpServletRequestBuilder restApiCall=MockMvcRequestBuilders.post("/event-gateway");
		restApiCall.header("Authorization", defaultToken);
		restApiCall.header("Tenant", defaultTenant);
		restApiCall.accept(MediaType.APPLICATION_JSON);
		restApiCall.contentType(MediaType.APPLICATION_JSON);
		
		File eventsJson = new File("src/test/resources/events.json");
		StringBuilder jsonString = new StringBuilder();
		BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(eventsJson)));
		String line;
		while ((line = buffer.readLine()) != null) {
			jsonString.append(line + System.lineSeparator());
		}
		buffer.close();
		restApiCall.content(jsonString.toString());

		ResultActions result = mvc.perform(restApiCall);
		result.andExpect(status().isAccepted()).andExpect(content().string(containsString("uuid")));
		
	}

}
