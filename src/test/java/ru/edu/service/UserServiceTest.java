package ru.edu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.edu.TestBase;
import ru.edu.dao.UserDao;
import ru.edu.dto.User;
import ru.edu.extension.ConditionalExtension;
import ru.edu.extension.GlobalExtension;
import ru.edu.extension.PostProcessingExtension;
import ru.edu.extension.ThrowableExtension;
import ru.edu.extension.UserServiceParamResolver;

// @TestInstance - жизненный цикл теста
// PER_METHOD задан по умолчанию (можно не указывать). Объект создается при каждом методе
@TestInstance(Lifecycle.PER_METHOD)
//@Tag("login")
// порядок запуска методов в тесте
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // методом добавлять  @Order
//@TestMethodOrder(MethodOrderer.MethodName.class) // в алфавитном порядке по названию
//@TestMethodOrder(MethodOrderer.DisplayName.class) // в алфавитном порядке по @DisplayName
//@ExtendWith на смену @RunWith
@ExtendWith({ // добавляем функциональность в класс
    UserServiceParamResolver.class, // этот резолвер возвращает объект при DI UserService
//    GlobalExtension.class // Можем наследовать расширения от родителя TestBase
    PostProcessingExtension.class, // свой пример, как работает спринг
    ConditionalExtension.class,
    MockitoExtension.class  // из mockito-junit-jupiter
    //ThrowableExtension.class
})
//@RunWith() // добавляем функциональность. Обычно для фреймворков (спринг). Использовалось в junit4
public class UserServiceTest extends TestBase {

  private static final User IVAN = User.of(1, "Ivan", "123");

  private static final User PETR = User.of(2, "Petr", "123");

  @Captor
  private ArgumentCaptor<Integer> argumentCaptor;

  @InjectMocks // куда делаем DI моков (@Mock)
  private UserService userService;

  // lenient - не кидать ошибку, если запланировали вызов метода у мока,
  // но не вызвали - НЕ РЕКОМЕНДУЕТСЯ
  @Mock(lenient = true) // создать до BeforeEach благодаря расширению MockitoExtension
  private UserDao userDao;

  // конструкторы разрешили только в junit5
  // Делает DI TestInfo (задан в дефолтном резолвере)
  UserServiceTest(TestInfo testInfo) {
    System.out.println();
  }

  @BeforeAll
  static void init() { // название неважно
    // void init() { можно не статический для Lifecycle.PER_CLASS
    System.out.println("Before all: ");
  }

  @BeforeEach
    // DI UserService определили в UserServiceParamResolver
  void prepare(UserService userService) { // название неважно
    System.out.println("Before each: " + this);

    // запланировали перед каждым тестом вызов метода у мока.
    // Кинет ошибку, если не вызвать.
    // Можно подавить ошибку @Mock(lenient = true) - но не стоит так делать
    Mockito.doReturn(true).when(userDao).delete(IVAN.getId());

//    this.userService = userService;

    // заменяем на @Mock из расширения MockitoExtension
//    this.userDao = Mockito.mock(UserDao.class); // mock
//    this.userDao = Mockito.spy(new UserDao()); //spy

    // заменяем на @InjectMocks из расширения MockitoExtension
//    this.userService = new UserService(this.userDao);
  }

  @Test
  void throwExceptionIfDatabaseIsNotAvailable() {
    Mockito.doThrow(RuntimeException.class).when(userDao).delete(IVAN.getId());
    assertThrows(RuntimeException.class, () -> userService.delete(IVAN.getId()));
  }

  @Test
  void shouldDeleteExistedUser() {
    userService.add(IVAN);

//    Mockito.doReturn(true).when(this.userDao).delete(IVAN.getId());
    // подходит для spy
    Mockito.doReturn(true).when(this.userDao).delete(Mockito.anyInt());
    // другой вариант написания мока, но выше предпочтительнее
    // не подходит для spy
//    Mockito.when(this.userDao.delete(Mockito.anyInt())).thenReturn(true);

    // так можем несколько раз обратиться (указывает ответ на каждое обращение)
//    Mockito.when(this.userDao.delete(Mockito.anyInt()))
//        .thenReturn(true)
//        .thenReturn(false);

    // еще ни разу не дернули этот spy
    Mockito.verifyNoInteractions(userDao);

    var deleteResult = userService.delete(IVAN.getId());

    // Представим, что в сервисе создается какой-то параметр Integer,
    // который мы не можем из вне указать в вызове мока.
    // Но можем указать, что метод вызовется где-то внутри с объектом такого класса
    // заменили на @Captor из расширения MockitoExtension
//    var argumentCaptor = ArgumentCaptor.forClass(Integer.class);

    // проверяем, что метод вызван 1 раз
    Mockito.verify(userDao).delete(argumentCaptor.capture());
    // проверяем, что метод вызван 3 раза
//    Mockito.verify(userDao, Mockito.times(3)).delete(IVAN.getId());

    // проверяем, что последнее значение прокинутое в метод мока равно тому-то
    assertThat(argumentCaptor.getValue()).isEqualTo(IVAN.getId());

    assertThat(deleteResult).isTrue();

    // Behavior Driven Development BDD
    BDDMockito.given(userDao.delete(IVAN.getId())).willReturn(true);
    // или
    BDDMockito.willReturn(true).given(userDao).delete(IVAN.getId());
  }

  @Test
  @Order(2)
  @DisplayName("users will be empty if no user added")
    // отображает в консоле название теста при выполнении
    // название должно явно говорить, что происходит
  void usersEmptyIfNoUserAdded() {
    System.out.println("Test1: " + this);

    var users = userService.getAll();
    assertTrue(users.isEmpty(), "User list should be empty");
  }

  @Test
  @Order(1)
  void usersSizeIfUserAdded() throws IOException {
//    if (true) {
//      // ThrowableExtension игнорируем все, кроме IOException
//      throw new RuntimeException("ошибка, как пример для своего расширения ThrowableExtension");
//    }

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
  @Timeout(value = 200, unit = TimeUnit.MILLISECONDS) // ограничить по времени все тесты в классе
  class LoginTest {

    @Test
    @Timeout(value = 200, unit = TimeUnit.MILLISECONDS)
      // ограничить по времени весь тест
    void checkLoginFunctionalityPerformance() {
      System.out.println(Thread.currentThread().getName()); // main
      var result1 = assertTimeout( // result - результат из нашей функциональности (тут User)
          Duration.ofMillis(200L), // время, которое даем на выполнение
          () -> { // тестируемая функциональность
            System.out.println(Thread.currentThread().getName()); // main
//            Thread.sleep(201L);
            return userService.login(IVAN.getUsername(), IVAN.getPassword());
          }
      );

      // assertTimeoutPreemptively запускает в отдельном потоке
      var result2 = assertTimeoutPreemptively(
          Duration.ofMillis(200L), // время, которое даем на выполнение
          () -> { // тестируемая функциональность
            System.out.println(Thread.currentThread().getName()); // junit-timeout-thread-1
//            Thread.sleep(201L);
            return userService.login(IVAN.getUsername(), IVAN.getPassword());
          }
      );
    }

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

    //    @Test
//    @Tag("login")
    @RepeatedTest(value = 5)
    // повторение теста
    void loginFailedIfPasswordIsNotCorrect(RepetitionInfo repetitionInfo) {
      System.out.println(repetitionInfo.getCurrentRepetition() + "/" + repetitionInfo.getTotalRepetitions());
      userService.add(IVAN);
      Optional<User> maybeUser = userService.login(IVAN.getUsername(), "dummy");

      assertTrue(maybeUser.isEmpty());
    }

    @Test
//    @Tag("login")
    @Disabled("flaky, need to see")
      // не запускает тест
    void loginFailedIfUserDoesNotExist() {
      userService.add(IVAN);
      Optional<User> maybeUser = userService.login("dummy", IVAN.getPassword());

      assertTrue(maybeUser.isEmpty());
    }

    // name - можем переопределить отображение тестов в консоле
    @ParameterizedTest(name = "{arguments} test")
//  @ArgumentsSource() // можем передавать несколько своих провайдеров данных
    // для NullSource и EmptySource в тесте должно быть только один параметр
//  @NullSource // встроенный провайдер данных NullArgumentsProvider
//  @EmptySource // встроенный провайдер данных EmptyArgumentsProvider
//  @NullAndEmptySource // объединяет NullArgumentsProvider и EmptyArgumentsProvider
//    @ValueSource(strings = { // встроенный провайдер данных ValueArgumentsProvider
//        "IVAN", "PETR"  // передаем по очереди параметры в переменную теста
//    })
//  @EnumSource   // провайдер EnumArgumentsProvider
    @MethodSource("ru.edu.service.UserServiceTest#getArgumentsForLoginTest") // используется как провайдер чаще всего
    // тянем данные из csc, но он не универсальный, тк объект уже через него не передашь
//    @CsvFileSource(resources = "/login-test-data.csv", delimiter = ',', numLinesToSkip = 1)
//    @CsvSource({ // тоже, что @CsvFileSource, но указываем строки вручную, а не через csv файл
//        "Ivan,123",
//        "Pert,123"
//    })
    @DisplayName("login param test")
    void loginParametrizedTest(String username, String password/*, Optional<User> user*/) {
      userService.add(IVAN, PETR);

      Optional<User> maybeUser = userService.login(username, password);
//      assertThat(user).isEqualTo(maybeUser);
    }

  }

  static Stream<Arguments> getArgumentsForLoginTest() {
    return Stream.of(
        Arguments.of("Ivan", "123", Optional.of(IVAN)),
        Arguments.of("Petr", "123", Optional.of(PETR)),
        Arguments.of("Petr", "dummy", Optional.empty()),
        Arguments.of("dummy", "123", Optional.empty())
    );
  }

}
