package ru.edu.service;

import java.util.ArrayList;
import java.util.List;
import ru.edu.dto.User;

public class UserService {

	private final List<User> users = new ArrayList<>();

	public List<User> getAll() {
		return users;
	}

	public boolean add(User user) {
		return users.add(user);
	}

}
