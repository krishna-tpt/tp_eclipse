package com.taxcalc.repository;

import com.taxcalc.model.User;

import java.io.*;
import java.util.*;

/**
 * Persists User objects to a flat file using Java Serialization.
 * File: data/users.dat
 */
public class UserRepository {

	private static final String DATA_DIR = "data";
	private static final String FILE_PATH = DATA_DIR + File.separator + "users.dat";

	private Map<String, User> users = new LinkedHashMap<>();

	public UserRepository() {
		ensureDataDir();
		load();
	}

	private void ensureDataDir() {
		File dir = new File(DATA_DIR);
		if (!dir.exists())
			dir.mkdirs();
	}

	/** Save a new or updated user */
	public void save(User user) {
		users.put(user.getUsername(), user);
		persist();
	}

	/** Find by username (case-insensitive) */
	public Optional<User> findByUsername(String username) {
		return Optional.ofNullable(users.get(username.toLowerCase()));
	}

	public Collection<User> findAll() {
		return Collections.unmodifiableCollection(users.values());
	}

	public boolean exists(String username) {
		return users.containsKey(username.toLowerCase());
	}

	public boolean delete(String username) {
		boolean removed = users.remove(username.toLowerCase()) != null;
		if (removed)
			persist();
		return removed;
	}

	@SuppressWarnings("unchecked")
	private void load() {
		File file = new File(FILE_PATH);
		if (!file.exists())
			return;
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
			users = (Map<String, User>) ois.readObject();
		} catch (Exception e) {
			System.out.println("  [WARN] Could not load users file — starting fresh. (" + e.getMessage() + ")");
			users = new LinkedHashMap<>();
		}
	}

	private void persist() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
			oos.writeObject(users);
		} catch (IOException e) {
			System.out.println("  [ERROR] Failed to save users: " + e.getMessage());
		}
	}
}
