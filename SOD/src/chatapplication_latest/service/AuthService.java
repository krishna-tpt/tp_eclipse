package chatapplication_latest.service;

import java.time.LocalDateTime;

import chatapplication_latest.enums.Role;
import chatapplication_latest.exception.AuthException;
import chatapplication_latest.model.Session;
import chatapplication_latest.model.User;
import chatapplication_latest.repository.SessionRepository;
import chatapplication_latest.repository.UserRepository;
import chatapplication_latest.util.EmailValidator;
import chatapplication_latest.util.PasswordHasher;

/**
 * Handles sign-up, sign-in, sign-out and session validation.
 * Both USER and ADMIN accounts go through the same flow; 
 * the role is specified at sign-up time.
 */
public class AuthService {

    private final UserRepository    userRepo;
    private final SessionRepository sessionRepo;

    public AuthService(UserRepository userRepo, SessionRepository sessionRepo) {
        this.userRepo    = userRepo;
        this.sessionRepo = sessionRepo;
    }

    // ── Sign-up ────────────────────────────────────────────────────

    /**
     * Register a new user (USER or ADMIN).
     *
     * @throws AuthException if email format invalid, or username/email already taken
     */
    public User signUp(String username, String email, String password, Role role) {
        // 1. Validate email format
        if (!EmailValidator.isValid(email)) {
            throw new AuthException("Invalid email format: " + email);
        }

        // 2. Uniqueness checks
        if (userRepo.existsByUsername(username)) {
            throw new AuthException("Username already taken: " + username);
        }
        if (userRepo.existsByEmail(email)) {
            throw new AuthException("Email already registered: " + email);
        }

        // 3. Hash password and persist
        String hash = PasswordHasher.hash(password);
        User user   = new User(username, email, hash, role);
        userRepo.save(user);

        return user;
    }

    // ── Sign-in ────────────────────────────────────────────────────

    /**
     * Authenticate with email + password.
     *
     * @return Session token string
     * @throws AuthException on bad credentials or inactive account
     */
    public String signIn(String email, String password) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new AuthException("No account found for: " + email));

        if (!user.isActive()) {
            throw new AuthException("Account is disabled");
        }

        if (!PasswordHasher.verify(password, user.getPasswordHash())) {
            throw new AuthException("Incorrect password");
        }

        user.setLastLoginAt(LocalDateTime.now());
        Session session = new Session(user.getId());
        sessionRepo.save(session);

        return session.getToken();
    }

    // ── Sign-out ───────────────────────────────────────────────────

    /**
     * Invalidate a session (sign out).
     */
    public void signOut(String token) {
        sessionRepo.invalidate(token);
    }

    // ── Session validation ─────────────────────────────────────────

    /**
     * Resolve a token to the owning User.
     *
     * @throws AuthException if token is missing, expired, or invalidated
     */
    public User resolveSession(String token) {
        Session session = sessionRepo.findByToken(token)
                .orElseThrow(() -> new AuthException("Invalid or expired session"));

        if (!session.isValid()) {
            throw new AuthException("Session expired");
        }

        return userRepo.findById(session.getUserId())
                .orElseThrow(() -> new AuthException("User not found for session"));
    }

    /**
     * Convenience: resolve session and assert ADMIN role.
     */
    public User resolveAdminSession(String token) {
        User user = resolveSession(token);
        if (!user.isAdmin()) {
            throw new AuthException("Admin access required");
        }
        return user;
    }
}
