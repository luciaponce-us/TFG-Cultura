package com.tfg.cultura.api.users.controller;

import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.model.dto.UserUpdateRequest;
import com.tfg.cultura.api.users.model.enumerators.Role;
import com.tfg.cultura.api.users.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users - CRUD", description = "Gestión de usuarios")
public class UserController implements UserControllerInterface {

	private final UserService userService;

	@Override
	@GetMapping
	public ResponseEntity<Page<UserResponse>> getAllUsers(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) Role role,
			@RequestParam(required = false) Boolean active, @RequestParam(required = false) String name) {
		Page<UserResponse> response = userService.getAllUsers(page, size, role, active, name);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Override
	@GetMapping("/{username}")
	public ResponseEntity<UserResponse> getUser(@PathVariable String username) {
		UserResponse response = userService.getUser(username);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Override
	@PutMapping("/{username}")
	public ResponseEntity<UserResponse> updateUser(@PathVariable String username,
			@RequestBody @Valid UserUpdateRequest request) {
		UserResponse response = userService.updateUser(username, request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Override
	@PutMapping(value = "/{username}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UserResponse> updateUserAvatar(@PathVariable String username,
			@RequestPart(value = "avatar") MultipartFile avatar) {
		UserResponse response = userService.updateUserAvatar(username, avatar);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Override
	@PutMapping("/{username}/toggle-activation")
	public ResponseEntity<UserResponse> toggleUserActivation(@PathVariable String username) {
		UserResponse response = userService.toggleUserActivation(username);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Override
	@DeleteMapping("/{username}")
	public ResponseEntity<Void> deleteUser(@PathVariable String username) {
		userService.deleteUser(username);
		return ResponseEntity.noContent().build();
	}

}
