package ru.edu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Optional;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import ru.edu.dto.User;

// @TestInstance - жизненный цикл теста
// PER_METHOD задан по умолчанию (можно не указывать). Объект создается при каждом методе
@TestInstance(Lifecycle.PER_METHOD)
//@Tag("login")
// порядок запуска методов в тесте
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // методом добавлять  @Order
//@TestMethodOrder(MethodOrderer.MethodName.class) // в алфавитном порядке по названию
//@TestMethodOrder(MethodOrderer.DisplayName.class) // в алфавитном порядке по @DisplayName
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
  @Order(2)
  @DisplayName("users will be empty if no user added") // отображает в консоле название теста при выполнении
    // название должно явно говорить, что происходит
  void usersEmptyIfNoUserAdded() {
    System.out.println("Test1: " + this);

    var users = userService.getAll();
    assertTrue(users.isEmpty(), "User list should be empty");
  }

  @Test
  @Order(1)
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
  @Tag("fast")
  @Tag("user")
  void throwExceptionIfUserOrPasswordIsNull() {
//		try {
//			userService.login(null, "dummy");
//			// junit fail вызывает throw new AssertionFailedError(message);
//			fail("login should throw exception on null username");
//		} catch (IllegalArgumentException e) {
//			assertTrue(true);
//		}

    // более короткий вариант. Не надо оборачивать в try
//		assertThrows(IllegalArgumentException.class, () -> userService.login(null, "dummy"));

    assertAll(
        () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, "dummy")),
        () -> {
          var e = assertThrows(IllegalArgumentException.class, () -> userService.login("dummy", null));
          assertThat(e.getMessage()).isEqualTo("username or password is null");
        }
    );
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

  @AfterEach
  void deleteDataFromDatabase() {
    System.out.println("After each: " + this);
  }

  @AfterAll
  static void closeConnectionPool() {
    // void closeConnectionPool() { можно не статический для Lifecycle.PER_CLASS
    System.out.println("After all: ");
  }

  // группируем тесты во внутреннем классе для повышения читаемости
  @Tag("login")
  @DisplayName("Test user login functionality")
  @Nested // чтобы его было видно, как обычный класс
  class LoginTest {
    @Test
//    @Tag("login")
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
//    @Tag("login")
    void loginFailedIfPasswordIsNotCorrect() {
      userService.add(IVAN);
      Optional<User> maybeUser = userService.login(IVAN.getUsername(), "dummy");

      assertTrue(maybeUser.isEmpty());
    }

    @Test
//    @Tag("login")
    void loginFailedIfUserDoesNotExist() {
      userService.add(IVAN);
      Optional<User> maybeUser = userService.login("dummy", IVAN.getPassword());

      assertTrue(maybeUser.isEmpty());
    }

  }

}
