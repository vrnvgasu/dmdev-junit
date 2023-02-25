package ru.edu.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import ru.edu.dto.User;

// @TestInstance - жизненный цикл теста
// PER_METHOD задан по умолчанию (можно не указывать). Объект создается при каждом методе
@TestInstance(Lifecycle.PER_METHOD)
class UserServiceTest {

	private UserService userService;

	@BeforeAll
	static void init() { // название неважно
	// void init() { можно не статический для Lifecycle.PER_CLASS
		System.out.println("Before all: ");
	}

	@BeforeEach
	void prepare() { // название неважно
		System.out.println("Before each: " + this);
		userService = new UserService();
	}

	@Test
	// название должно явно говорить, что происходит
	void usersEmptyIfNoUserAdded() {
		System.out.println("Test1: " + this);

		var users = userService.getAll();
		assertTrue(users.isEmpty(), () -> "User list should be empty");
	}

	@Test
	void usersSizeIfUserAdded() {
		System.out.println("Test2: " + this);
		userService.add(new User());
		userService.add(new User());
		var users = userService.getAll();
		assertEquals(2, users.size());
	}

	@AfterEach
	void deleteDataFromDatabase() {
		System.out.println("After each: " + this);
	}

	@AfterAll
	static void closeConnectionPool() {
		// void closeConnectionPool() { можно не статический для Lifecycle.PER_CLASS
		System.out.println("After all: ");
	}

}
