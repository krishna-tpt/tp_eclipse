package chatapplication_latest.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents an authenticated session token for a User.
 */
public class Session {

    private final String token;
    private final String userId;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean invalidated;

    private static final int SESSION_HOURS = 24;

    public Session(String userId) {
        this.token       = UUID.randomUUID().toString();
        this.userId      = userId;
        this.createdAt   = LocalDateTime.now();
        this.expiresAt   = createdAt.plusHours(SESSION_HOURS);
        this.invalidated = false;
    }

    public String        getToken()      { return token; }
    public String        getUserId()     { return userId; }
    public LocalDateTime getCreatedAt()  { return createdAt; }
    public LocalDateTime getExpiresAt()  { return expiresAt; }
    public boolean       isInvalidated() { return invalidated; }

    public boolean isValid() {
        return !invalidated && LocalDateTime.now().isBefore(expiresAt);
    }

    public void invalidate() {
        this.invalidated = true;
    }
}
