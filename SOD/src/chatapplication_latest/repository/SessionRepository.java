package chatapplication_latest.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import chatapplication_latest.model.Session;

/**
 * In-memory session store (token → Session).
 */
public class SessionRepository {

    private final Map<String, Session> byToken = new ConcurrentHashMap<>();

    public void save(Session session) {
        byToken.put(session.getToken(), session);
    }

    public Optional<Session> findByToken(String token) {
        return Optional.ofNullable(byToken.get(token));
    }

    public void invalidate(String token) {
        byToken.computeIfPresent(token, (k, s) -> { s.invalidate(); return s; });
    }

    public void removeExpired() {
        byToken.values().removeIf(s -> !s.isValid());
    }
}
