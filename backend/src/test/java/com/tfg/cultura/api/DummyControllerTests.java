package com.tfg.cultura.api;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;

import com.tfg.cultura.api.core.controller.DummyController;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class DummyControllerTests {

	@Mock
	MongoTemplate mongoTemplate;

	@InjectMocks
	DummyController controller;

	@Test
	void getDummyDataReturnsExpectedMessage() {
		String result = controller.getDummyData();

		assertEquals("This is some dummy data from the API!", result);
	}

	@Test
	void getMongoDbDataReturnsOkWhenPingSucceeds() {
		MongoDatabase mongoDatabase = mock(MongoDatabase.class);

		when(mongoTemplate.getDb()).thenReturn(mongoDatabase);
		when(mongoDatabase.runCommand(any(Document.class))).thenReturn(new Document("ok", 1));

		ResponseEntity<String> result = controller.getMongodbData();

		assertTrue(result.getStatusCode().is2xxSuccessful());
	}

	@Test
	void getMongoDbDataReturnsErrorWhenPingFails() {
		MongoDatabase mongoDatabase = mock(MongoDatabase.class);

		when(mongoTemplate.getDb()).thenReturn(mongoDatabase);
		when(mongoDatabase.runCommand(any(Document.class))).thenThrow(new RuntimeException("ping failed"));

		ResponseEntity<String> result = controller.getMongodbData();

		assertTrue(result.getStatusCode().is5xxServerError());
	}
}
