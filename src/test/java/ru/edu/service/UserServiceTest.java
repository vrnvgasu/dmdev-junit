package ru.edu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Optional;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsMapContaining;
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
public class UserServiceTest {

	private static final User IVAN = User.of(1, "Ivan", "123");

	private static final User PETR = User.of(2, "Petr", "123");

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
		assertTrue(users.isEmpty(), "User list should be empty");
	}

	@Test
	void usersSizeIfUserAdded() {
		System.out.println("Test2: " + this);
		userService.add(IVAN);
		userService.add(PETR);
		var users = userService.getAll();

		//AssertJ - передали результат,
		// а потом проверяем у него разные параметры
		assertThat(users)
				.hasSize(2);

//		assertEquals(2, users.size());
	}

	@Test
	void loginSuccessIfUserExist() {
		userService.add(IVAN);
		Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());

		//AssertJ
		assertThat(maybeUser)
				.isPresent();
		maybeUser.ifPresent(user -> assertThat(user).isEqualTo(IVAN));

//		assertTrue(maybeUser.isPresent());
//		maybeUser.ifPresent(user -> assertEquals(IVAN, user));
	}

	@Test
	void usersConvertedToMapById() {
		userService.add(IVAN, PETR);
		Map<Integer, User> users = userService.getAllConvertedById();

		// junit
		assertAll( // проверит все ассерты, даже если первый упадет
				//AssertJ
				() -> assertThat(users).containsKeys(IVAN.getId(), PETR.getId()),
				() -> assertThat(users).containsValues(IVAN, PETR)
		);

		// Hamcrest. 2 параметра: результат и ожидаемый
		MatcherAssert.assertThat(users, IsMapContaining.hasKey(IVAN.getId()));
	}

	@Test
	void loginFailedIfPasswordIsNotCorrect() {
		userService.add(IVAN);
		Optional<User> maybeUser = userService.login(IVAN.getUsername(), "dummy");

		assertTrue(maybeUser.isEmpty());
	}

	@Test
	void loginFailedIfUserDoesNotExist() {
		userService.add(IVAN);
		Optional<User> maybeUser = userService.login("dummy", IVAN.getPassword());

		assertTrue(maybeUser.isEmpty());
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
