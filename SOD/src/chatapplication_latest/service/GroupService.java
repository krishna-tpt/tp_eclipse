package chatapplication_latest.service;

import java.util.List;

import chatapplication_latest.exception.AuthException;
import chatapplication_latest.model.Group;
import chatapplication_latest.model.User;
import chatapplication_latest.repository.GroupRepository;
import chatapplication_latest.repository.UserRepository;

/**
 * Business logic for Group Chat management.
 * Only admins can create groups and manage membership.
 * Members can post and comment inside a group.
 */
public class GroupService {

    private final GroupRepository groupRepo;
    private final UserRepository  userRepo;
    private final AuthService     authService;

    public GroupService(GroupRepository groupRepo,
                        UserRepository userRepo,
                        AuthService authService) {
        this.groupRepo   = groupRepo;
        this.userRepo    = userRepo;
        this.authService = authService;
    }

    // ── Admin-only: group lifecycle ────────────────────────────────

    /**
     * Admin: create a new group chat.
     */
    public Group createGroup(String adminToken, String name, String description) {
        User admin = authService.resolveAdminSession(adminToken);
        Group group = new Group(name, description, admin.getId());
        groupRepo.save(group);
        return group;
    }

    /**
     * Admin: add a user to a group.
     */
    public void addMember(String adminToken, String groupId, String userId) {
        authService.resolveAdminSession(adminToken);
        Group group = getGroupOrThrow(groupId);
        userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        group.addMember(userId);
        groupRepo.save(group);
    }

    /**
     * Admin: remove a user from a group.
     */
    public void removeMember(String adminToken, String groupId, String userId) {
        authService.resolveAdminSession(adminToken);
        Group group = getGroupOrThrow(groupId);
        group.removeMember(userId);
        groupRepo.save(group);
    }

    /**
     * Admin: promote a member to group admin.
     */
    public void promoteToAdmin(String adminToken, String groupId, String userId) {
        authService.resolveAdminSession(adminToken);
        Group group = getGroupOrThrow(groupId);
        if (!group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group: " + userId);
        }
        group.promoteToAdmin(userId);
        groupRepo.save(group);
    }

    // ── Any member: read ───────────────────────────────────────────

    /**
     * Get all groups the calling user belongs to.
     */
    public List<Group> getMyGroups(String token) {
        User user = authService.resolveSession(token);
        return groupRepo.findByMemberId(user.getId());
    }

    /**
     * Get a single group (caller must be a member, or admin).
     */
    public Group getGroup(String token, String groupId) {
        User user  = authService.resolveSession(token);
        Group group = getGroupOrThrow(groupId);
        if (!user.isAdmin() && !group.isMember(user.getId())) {
            throw new AuthException("Not a member of this group");
        }
        return group;
    }

    /**
     * Admin: list all groups.
     */
    public List<Group> getAllGroups(String adminToken) {
        authService.resolveAdminSession(adminToken);
        return groupRepo.findAll();
    }

    // ── Helpers ────────────────────────────────────────────────────

    private Group getGroupOrThrow(String groupId) {
        return groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupId));
    }
}
