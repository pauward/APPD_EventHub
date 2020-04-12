package com.appd.mvp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchProcessorBlockTests {
	
	static final Logger logger = LoggerFactory.getLogger(BatchProcessorBlockTests.class);
	
	@Test
	void testInit() {
		logger.debug("@TEST : Check Bean Initialization");

		BatchProcessor batchEngine = new BatchProcessor(1000, 15);
		assertTrue(batchEngine.batchQueueSize == 1000);
		assertTrue(batchEngine.batchCycleTime == 15);
		
		logger.debug("@TEST : Successful test");
	}
}
