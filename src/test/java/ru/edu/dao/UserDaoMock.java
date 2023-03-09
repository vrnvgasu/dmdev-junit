package ru.edu.dao;

import java.util.HashMap;
import java.util.Map;
import org.mockito.stubbing.Answer1;

// По такой логике работает Mock и спринг.
// Наследуются от объекта через рефлексию и соответственно, его можно использовать, как заглушку
public class UserDaoMock extends UserDao {

  // Пример, как делается Mockito.anyInt()
  private Map<Integer, Boolean> answers = new HashMap<>();

  // так выглядит наш answers в мокито
//  private Answer1<Integer, Boolean> answer1;

  @Override
  public boolean delete(Integer userId) {
    // Mockito.anyInt()
    return answers.getOrDefault(userId, false);
  }

}
