package com.appd.mvp;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
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
		mvc.perform(MockMvcRequestBuilders.post("/event-gateway").header("Authorization", defaultToken)
				.header("Tenant", defaultTenant).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().string(containsString("")));
		System.out.println(content().toString());
	}

}
