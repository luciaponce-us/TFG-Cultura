package com.tfg.cultura.api.users.repository;

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
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

	@Mock
	private MongoTemplate mongoTemplate;

	@InjectMocks
	private UserRepositoryImpl repository;

	private User user;

	@BeforeEach
	void setUp() {
		user = UserFactory.validUser();
	}

	@Test
	void should_apply_role_and_active_filters_when_find_all_with_filters() {
		when(mongoTemplate.count(any(Query.class), eq(User.class))).thenReturn(1L);
		when(mongoTemplate.find(any(Query.class), eq(User.class))).thenReturn(List.of(user));

		Page<User> result = repository.findAllWithFilters(Role.SOCIO, true, null, PageRequest.of(0, 10));

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());

		ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
		verify(mongoTemplate).count(queryCaptor.capture(), eq(User.class));

		Document queryObject = queryCaptor.getValue().getQueryObject();
		assertEquals(Role.SOCIO, queryObject.get("role"));
		assertEquals(true, queryObject.get("active"));
		assertEquals(false, queryObject.containsKey("$and"));
	}

	@Test
	void should_apply_name_tokens_when_find_all_with_filters() {
		when(mongoTemplate.count(any(Query.class), eq(User.class))).thenReturn(1L);
		when(mongoTemplate.find(any(Query.class), eq(User.class))).thenReturn(List.of(user));

		repository.findAllWithFilters(null, null, "Ana Lopez", PageRequest.of(0, 10));

		ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
		verify(mongoTemplate).count(queryCaptor.capture(), eq(User.class));

		Document queryObject = queryCaptor.getValue().getQueryObject();
		List<?> andCriteria = (List<?>) queryObject.get("$and");
		assertNotNull(andCriteria);
		assertEquals(2, andCriteria.size());

		Document firstToken = (Document) andCriteria.get(0);
		List<?> orCriteria = (List<?>) firstToken.get("$or");
		assertNotNull(orCriteria);
		assertEquals(3, orCriteria.size());
	}

	@Test
	void should_ignore_blank_name_when_find_all_with_filters() {
		when(mongoTemplate.count(any(Query.class), eq(User.class))).thenReturn(1L);
		when(mongoTemplate.find(any(Query.class), eq(User.class))).thenReturn(List.of(user));

		repository.findAllWithFilters(null, null, "   ", PageRequest.of(0, 10));

		ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
		verify(mongoTemplate).count(queryCaptor.capture(), eq(User.class));

		Document queryObject = queryCaptor.getValue().getQueryObject();
		assertTrue(queryObject.isEmpty());
	}

	@Test
	void should_apply_pagination_and_sort_when_find_all_with_filters() {
		PageRequest pageable = PageRequest.of(1, 5, Sort.by("createdAt").descending());

		when(mongoTemplate.count(any(Query.class), eq(User.class))).thenReturn(1L);
		when(mongoTemplate.find(any(Query.class), eq(User.class))).thenReturn(List.of(user));

		repository.findAllWithFilters(null, null, null, pageable);

		ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
		verify(mongoTemplate).find(queryCaptor.capture(), eq(User.class));

		Query query = queryCaptor.getValue();
		assertEquals(5, query.getLimit());
		assertEquals(5, query.getSkip());
		assertEquals(-1, query.getSortObject().get("createdAt"));
	}
}
