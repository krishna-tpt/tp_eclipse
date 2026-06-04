package chatapplication_latest.service;

import java.util.List;

import chatapplication_latest.model.User;
import chatapplication_latest.repository.UserRepository;

/**
 * Admin-only operations on User accounts
 * (disable, enable, list all users).
 */
public class AdminService {

    private final UserRepository userRepo;
    private final AuthService    authService;

    public AdminService(UserRepository userRepo, AuthService authService) {
        this.userRepo    = userRepo;
        this.authService = authService;
    }

    /**
     * Admin: list all registered users.
     */
    public List<User> listAllUsers(String adminToken) {
        authService.resolveAdminSession(adminToken);
        return userRepo.findAll();
    }

    /**
     * Admin: disable a user account.
     */
    public void disableUser(String adminToken, String targetUserId) {
        authService.resolveAdminSession(adminToken);
        User target = getUserOrThrow(targetUserId);
        target.setActive(false);
        userRepo.save(target);
    }

    /**
     * Admin: re-enable a user account.
     */
    public void enableUser(String adminToken, String targetUserId) {
        authService.resolveAdminSession(adminToken);
        User target = getUserOrThrow(targetUserId);
        target.setActive(true);
        userRepo.save(target);
    }

    // ── Helpers ────────────────────────────────────────────────────

    private User getUserOrThrow(String userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
    }
}
