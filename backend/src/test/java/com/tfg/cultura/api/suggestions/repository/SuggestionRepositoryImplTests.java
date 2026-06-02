package com.tfg.cultura.api.suggestions.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.tfg.cultura.api.suggestions.factory.SuggestionFactory;
import com.tfg.cultura.api.suggestions.model.Suggestion;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;

@ExtendWith(MockitoExtension.class)
class SuggestionRepositoryImplTests {

	@Mock
	private MongoTemplate mongoTemplate;

	@InjectMocks
	private SuggestionRepositoryImpl repository;

	private Suggestion suggestion;

	@BeforeEach
	void setUp() {
		suggestion = SuggestionFactory.validSuggestion();
	}

	@Test
	void findAllWithFilters_should_apply_paging_without_filters() {
		Pageable pageable = PageRequest.of(1, 5);
		when(mongoTemplate.count(any(Query.class), eq(Suggestion.class))).thenReturn(1L);
		when(mongoTemplate.find(any(Query.class), eq(Suggestion.class))).thenReturn(List.of(suggestion));

		Page<Suggestion> result = repository.findAllWithFilters(null, null, null, null, pageable);

		assertNotNull(result);
		assertEquals(1, result.getContent().size());

		ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
		verify(mongoTemplate).find(queryCaptor.capture(), eq(Suggestion.class));
		Query query = queryCaptor.getValue();

		assertEquals(5, query.getLimit());
		assertEquals(5L, query.getSkip());
		assertTrue(query.getQueryObject().isEmpty());
	}

	@Test
	void findAllWithFilters_should_filter_by_text_tokens_and_admin_support() {
		Pageable pageable = PageRequest.of(0, 10);
		User secretary = UserFactory.validUser();
		secretary.setId("2");
		secretary.setRole(Role.SECRETARIO);
		User coordinator = UserFactory.validUser2();
		coordinator.setId("3");
		coordinator.setRole(Role.COORDINADOR);
		when(mongoTemplate.count(any(Query.class), eq(Suggestion.class))).thenReturn(1L);
		when(mongoTemplate.find(any(Query.class), eq(Suggestion.class))).thenReturn(List.of(suggestion));
		when(mongoTemplate.find(any(Query.class), eq(User.class))).thenReturn(List.of(secretary, coordinator));

		repository.findAllWithFilters(null, "first second", true, null, pageable);

		ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
		verify(mongoTemplate).find(queryCaptor.capture(), eq(Suggestion.class));
		Query query = queryCaptor.getValue();
		Document criteria = query.getQueryObject();

		assertTrue(criteria.containsKey("$and"));

		List<?> andCriteria = (List<?>) criteria.get("$and");
		Document supportersId = andCriteria.stream()
				.filter(Document.class::isInstance)
				.map(Document.class::cast)
				.filter(doc -> doc.containsKey("supportersId"))
				.findFirst()
				.orElseThrow();
		List<?> ids = (List<?>) ((Document) supportersId.get("supportersId")).get("$in");
		assertTrue(ids.contains("2"));
		assertTrue(ids.contains("3"));
	}

}
