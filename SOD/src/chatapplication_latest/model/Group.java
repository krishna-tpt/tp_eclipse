package chatapplication_latest.model;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a Group Chat.
 * A group has one or more admins and a set of members.
 * Posts inside the group follow PostVisibility rules.
 */
public class Group {

    private final String id;
    private String name;
    private String description;
    private final String createdByAdminId;        // only admins can create groups
    private Set<String> memberIds;
    private Set<String> adminIds;
    private List<Post> posts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Group(String name, String description, String createdByAdminId) {
        this.id                = UUID.randomUUID().toString();
        this.name              = name;
        this.description       = description;
        this.createdByAdminId  = createdByAdminId;
        this.memberIds         = new HashSet<>();
        this.adminIds          = new HashSet<>();
        this.posts             = new ArrayList<>();
        this.createdAt         = LocalDateTime.now();
        this.updatedAt         = LocalDateTime.now();

        // Creator becomes first admin of the group
        this.adminIds.add(createdByAdminId);
        this.memberIds.add(createdByAdminId);
    }

    // ── Getters ────────────────────────────────────────────────────
    public String       getId()               { return id; }
    public String       getName()             { return name; }
    public String       getDescription()      { return description; }
    public String       getCreatedByAdminId() { return createdByAdminId; }
    public Set<String>  getMemberIds()        { return Collections.unmodifiableSet(memberIds); }
    public Set<String>  getAdminIds()         { return Collections.unmodifiableSet(adminIds); }
    public List<Post>   getPosts()            { return Collections.unmodifiableList(posts); }
    public LocalDateTime getCreatedAt()       { return createdAt; }
    public LocalDateTime getUpdatedAt()       { return updatedAt; }

    // ── Member management ──────────────────────────────────────────
    public void addMember(String userId) {
        memberIds.add(userId);
        updatedAt = LocalDateTime.now();
    }

    public void removeMember(String userId) {
        memberIds.remove(userId);
        adminIds.remove(userId);
        updatedAt = LocalDateTime.now();
    }

    public boolean isMember(String userId) {
        return memberIds.contains(userId);
    }

    public boolean isAdmin(String userId) {
        return adminIds.contains(userId);
    }

    public void promoteToAdmin(String userId) {
        if (memberIds.contains(userId)) {
            adminIds.add(userId);
            updatedAt = LocalDateTime.now();
        }
    }

    // ── Post management ────────────────────────────────────────────
    public void addPost(Post post) {
        posts.add(post);
        updatedAt = LocalDateTime.now();
    }

    public void setName(String name) {
        this.name      = name;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Group{id='" + id + "', name='" + name + "', members=" + memberIds.size() + "}";
    }
}
