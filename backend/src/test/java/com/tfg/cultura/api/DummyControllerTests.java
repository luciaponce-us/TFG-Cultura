package com.tfg.cultura.api;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import com.tfg.cultura.api.core.controller.DummyController;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DummyControllerTests {

	@Test
	void getDummyDataReturnsExpectedMessage() throws Exception {
		DummyController controller = new DummyController();

		String result = controller.getDummyData();

		assertEquals("This is some dummy data from the API!", result);
	}

	@Test
	void getMongoDbDataReturnsOkWhenPingSucceeds() throws Exception {
		DummyController controller = new DummyController();
		MongoTemplate mongoTemplate = mock(MongoTemplate.class);
		MongoDatabase mongoDatabase = mock(MongoDatabase.class);

		when(mongoTemplate.getDb()).thenReturn(mongoDatabase);
		when(mongoDatabase.runCommand(any(Document.class))).thenReturn(new Document("ok", 1));
		ReflectionTestUtils.setField(controller, "mongoTemplate", mongoTemplate);

		String result = controller.getMongodbData();

		assertEquals("MongoDB OK", result);
	}

	@Test
	void getMongoDbDataReturnsErrorWhenPingFails() throws Exception {
		DummyController controller = new DummyController();
		MongoTemplate mongoTemplate = mock(MongoTemplate.class);
		MongoDatabase mongoDatabase = mock(MongoDatabase.class);

		when(mongoTemplate.getDb()).thenReturn(mongoDatabase);
		when(mongoDatabase.runCommand(any(Document.class))).thenThrow(new RuntimeException("ping failed"));
		ReflectionTestUtils.setField(controller, "mongoTemplate", mongoTemplate);

		String result = controller.getMongodbData();

		assertEquals("MongoDB ERROR: ping failed", result);
	}
}
