package com.tfg.cultura.api.users.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.exception.UsersExceptionHandler;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.dto.UserProfileUpdateRequest;
import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.service.UserService;
import com.tfg.cultura.api.utils.BaseControllerTest;

class UserProfileControllerTest extends BaseControllerTest {

	@Mock
	private UserService service;

	private static final String BASE_URL = "/api/users/profile";

	private UserResponse userResponse;
	private User user;
	private UserProfileUpdateRequest updateRequest;
	private MockMultipartFile avatar;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		UserProfileController controller = new UserProfileController(service);
		mockMvc = buildMockMvc(controller, UsersExceptionHandler.class);

		initTestData();
	}

	private void initTestData() {
		userResponse = UserFactory.validUserResponse();
		user = UserFactory.validUser();
		updateRequest = UserFactory.validUserProfileUpdateRequest();
		avatar = new MockMultipartFile(
				"avatar",
				"avatar.png",
				MediaType.IMAGE_PNG_VALUE,
				"fake-image-content".getBytes());
	}

	// ================ GET PROFILE ================

	@Test
	void should_return_user_profile() throws Exception {
		when(service.getProfile()).thenReturn(userResponse);

		mockMvc.perform(get(BASE_URL)) // ajusta la ruta si es distinta
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value(user.getUsername()));

		verify(service).getProfile();
	}

	@Test
	void should_return_404_when_user_not_found() throws Exception {
		when(service.getProfile())
				.thenThrow(new UserNotFoundException("User not found"));

		mockMvc.perform(get(BASE_URL))
				.andExpect(status().isNotFound());

		verify(service).getProfile();
	}

	// ================ UPDATE PROFILE ================

	@Test
	void should_update_profile_successfully() throws Exception {
		user.setUsername(updateRequest.getUsername());

		UserResponse response = new UserResponse(user);

		when(service.updateProfile(any()))
				.thenReturn(response);

		mockMvc.perform(put(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(updateRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value(user.getUsername()));

		verify(service).updateProfile(any());
	}

	@Test
	void should_return_400_when_validation_fails() throws Exception {

		UserProfileUpdateRequest request = UserProfileUpdateRequest.builder()
				.username("ab") // ❌ < 3
				.password("123") // ❌ < 8
				.email("email-mal") // ❌ inválido
				.build();

		mockMvc.perform(put(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(request)))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(service);
	}

	@Test
	void should_return_404_when_user_not_found_at_update() throws Exception {
		when(service.updateProfile(any()))
				.thenThrow(new UserNotFoundException("User not found"));

		mockMvc.perform(put(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(updateRequest)))
				.andExpect(status().isNotFound());

		verify(service).updateProfile(any());
	}

	// ================ DELETE PROFILE ================

	@Test
	void should_delete_profile_successfully() throws Exception {

		doNothing().when(service).deleteProfile();

		mockMvc.perform(delete(BASE_URL))
				.andExpect(status().isNoContent());

		verify(service).deleteProfile();
	}

	@Test
	void should_return_404_when_user_not_found_at_deleting() throws Exception {

		doThrow(new UserNotFoundException("User not found"))
				.when(service).deleteProfile();

		mockMvc.perform(delete(BASE_URL))
				.andExpect(status().isNotFound());

		verify(service).deleteProfile();
	}

	// UPDATE MY AVATAR

	@Test
	void shouldUpdateMyAvatarSuccessfully() throws Exception {
		when(service.updateCurrentUserAvatar(any(MultipartFile.class)))
				.thenReturn(userResponse);

		mockMvc.perform(multipart(BASE_URL + "/avatar")
				.file(avatar)
				.with(request -> {
					request.setMethod("PUT"); // 👈 clave para PUT multipart
					return request;
				})
				.contentType(MediaType.MULTIPART_FORM_DATA))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value(userResponse.getUsername()))
				.andExpect(jsonPath("$.email").value(userResponse.getEmail()));

		verify(service).updateCurrentUserAvatar(any(MultipartFile.class));
	}

	@Test
	void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {

		when(service.updateCurrentUserAvatar(any()))
				.thenThrow(new UserNotFoundException("User not found"));

		mockMvc.perform(multipart(BASE_URL + "/avatar")
				.file(avatar)
				.with(request -> {
					request.setMethod("PUT");
					return request;
				}))
				.andExpect(status().isNotFound());
	}

}
