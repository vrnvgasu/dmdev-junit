package ru.edu.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class UserServiceTest {

	@Test
	// название должно явно говорить, что происходит
	void usersEmptyIfNoUserAdded() {
		var userService = new UserService();
		var users = userService.getAll();
		assertTrue(users.isEmpty(), () -> "User list should be empty");
	}

}
