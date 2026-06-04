package com.taxcalc.service;

import com.taxcalc.model.User;
import com.taxcalc.repository.UserRepository;
import com.taxcalc.util.PasswordUtil;

import java.util.Collection;
import java.util.Optional;

public class AuthService {

	private final UserRepository userRepository;
	private User loggedInUser;

	public AuthService(UserRepository userRepository) {
		this.userRepository = userRepository;
		seedDefaultAdmin();
	}

	/** Ensure at least one admin exists on first run */
	private void seedDefaultAdmin() {
		if (userRepository.findAll().stream().noneMatch(u -> u.getRole() == User.Role.ADMIN)) {
			String hash = PasswordUtil.hash("admin", "admin123");
			userRepository.save(new User("admin", hash, User.Role.ADMIN, 0));
			System.out.println("  [INFO] Default admin created → username: admin | password: admin123");
		}
	}

	public boolean login(String username, String plainPassword) {
		String lc = username.toLowerCase();
		Optional<User> opt = userRepository.findByUsername(lc);
		if (opt.isEmpty())
			return false;
		User user = opt.get();
		if (!PasswordUtil.verify(lc, plainPassword, user.getPasswordHash()))
			return false;
		loggedInUser = user;
		return true;
	}

	public void logout() {
		loggedInUser = null;
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	public boolean isLoggedIn() {
		return loggedInUser != null;
	}

	public boolean registerUser(String username, String plainPassword, User.Role role, double income) {
		String lc = username.toLowerCase();
		if (userRepository.exists(lc))
			return false;
		if (username.trim().isEmpty())
			return false;
		String hash = PasswordUtil.hash(lc, plainPassword);
		userRepository.save(new User(lc, hash, role, income));
		return true;
	}

	public boolean deleteUser(String username) {
		return userRepository.delete(username.toLowerCase());
	}

	public Collection<User> getAllUsers() {
		return userRepository.findAll();
	}

	public Optional<User> findUser(String username) {
		return userRepository.findByUsername(username.toLowerCase());
	}

	public void updateUser(User user) {
		userRepository.save(user);
	}
}
