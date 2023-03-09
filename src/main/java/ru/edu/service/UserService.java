package ru.edu.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import ru.edu.dao.UserDao;
import ru.edu.dto.User;

public class UserService {

	private final UserDao userDao;

	private final List<User> users = new ArrayList<>();

	public UserService(UserDao userDao) {
		this.userDao = userDao;
	}

	public boolean delete(Integer userId) {
		return userDao.delete(userId);
	}

	public List<User> getAll() {
		return users;
	}

	public boolean add(User user) {
		return users.add(user);
	}

	public void add(User... users) {
		this.users.addAll(List.of(users));
	}

	public Optional<User> login(String username, String password) {
		if (username == null || password == null) {
			throw new IllegalArgumentException("username or password is null");
		}

		return users.stream()
				.filter(user -> user.getUsername().equals(username))
				.filter(user -> user.getPassword().equals(password))
				.findFirst();
	}

	public Map<Integer, User> getAllConvertedById() {
		return users.stream()
				.collect(Collectors.toMap(User::getId, Function.identity()));
	}

}
