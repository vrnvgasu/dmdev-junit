package ru.edu.dao;

import java.util.HashMap;
import java.util.Map;

public class UserDaoSpy extends UserDao {

  private final UserDao userDao;

  // Пример, как делается Mockito.anyInt()
  private Map<Integer, Boolean> answers = new HashMap<>();

  // в отличие от мока храним реальный объект в spy
  public UserDaoSpy(UserDao userDao) {
    this.userDao = userDao;
  }

  @Override
  public boolean delete(Integer userId) {
    // Если у нас не замокан метод,
    // то в spy вызываем метод реального объекта
    return answers.getOrDefault(userId, userDao.delete(userId));
  }

}
