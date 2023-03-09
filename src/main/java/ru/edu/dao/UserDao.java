package ru.edu.dao;

import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.SneakyThrows;

public class UserDao {

  @SneakyThrows
  public boolean delete(Integer userId) {
    try(var connection = DriverManager.getConnection("url", "username", "password")) {
      return true;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}
