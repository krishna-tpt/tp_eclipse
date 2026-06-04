package chatapplication_latest.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import chatapplication_latest.model.User;

/**
 * In-memory store for User entities.
 * Swap with a JPA/database-backed implementation in production.
 */
public class UserRepository {

    // Primary store: id → User
    private final Map<String, User> byId       = new ConcurrentHashMap<>();
    // Unique-constraint indexes
    private final Map<String, String> byEmail    = new ConcurrentHashMap<>();  // email → id
    private final Map<String, String> byUsername = new ConcurrentHashMap<>();  // username → id

    public void save(User user) {
        byId.put(user.getId(), user);
        byEmail.put(user.getEmail().toLowerCase(), user.getId());
        byUsername.put(user.getUsername().toLowerCase(), user.getId());
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    public Optional<User> findByEmail(String email) {
        String id = byEmail.get(email.toLowerCase());
        return Optional.ofNullable(id).map(byId::get);
    }

    public Optional<User> findByUsername(String username) {
        String id = byUsername.get(username.toLowerCase());
        return Optional.ofNullable(id).map(byId::get);
    }

    public boolean existsByEmail(String email) {
        return byEmail.containsKey(email.toLowerCase());
    }

    public boolean existsByUsername(String username) {
        return byUsername.containsKey(username.toLowerCase());
    }

    public List<User> findAll() {
        return new ArrayList<>(byId.values());
    }
}
