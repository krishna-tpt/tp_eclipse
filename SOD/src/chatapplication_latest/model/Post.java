package chatapplication_latest.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import chatapplication_latest.enums.PostVisibility;

/**
 * A Post may belong to a user's feed OR to a Group chat.
 * If groupId is null the post is a personal/feed post.
 * visibility controls who can see it (admin-controlled feature).
 */
public class Post {

    private final String id;
    private final String authorId;
    private String content;
    private String groupId;                        // null ⇒ feed post
    private PostVisibility visibility;
    private Set<String> allowedUserIds;            // for SELECTED_MEMBERS visibility
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Comment> comments;

    public Post(String authorId, String content, String groupId) {
        this.id             = UUID.randomUUID().toString();
        this.authorId       = authorId;
        this.content        = content;
        this.groupId        = groupId;
        this.visibility     = PostVisibility.PUBLIC;
        this.allowedUserIds = new HashSet<>();
        this.deleted        = false;
        this.createdAt      = LocalDateTime.now();
        this.updatedAt      = LocalDateTime.now();
        this.comments       = new ArrayList<>();
    }

    // ── Getters ────────────────────────────────────────────────────
    public String          getId()             { return id; }
    public String          getAuthorId()       { return authorId; }
    public String          getContent()        { return content; }
    public String          getGroupId()        { return groupId; }
    public PostVisibility  getVisibility()     { return visibility; }
    public Set<String>     getAllowedUserIds()  { return Collections.unmodifiableSet(allowedUserIds); }
    public boolean         isDeleted()         { return deleted; }
    public LocalDateTime   getCreatedAt()      { return createdAt; }
    public LocalDateTime   getUpdatedAt()      { return updatedAt; }
    public List<Comment>   getComments()       { return Collections.unmodifiableList(comments); }

    // ── Setters / mutators ─────────────────────────────────────────
    public void setContent(String content) {
        this.content   = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void setVisibility(PostVisibility visibility) {
        this.visibility = visibility;
        this.updatedAt  = LocalDateTime.now();
    }

    public void addAllowedUser(String userId) {
        this.allowedUserIds.add(userId);
    }

    public void removeAllowedUser(String userId) {
        this.allowedUserIds.remove(userId);
    }

    public void markDeleted() {
        this.deleted   = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    /**
     * Checks if a given user can view this post.
     */
    public boolean isVisibleTo(String userId) {
        return switch (visibility) {
            case PUBLIC      -> !deleted;
            case GROUP_ONLY  -> !deleted;              // group membership checked upstream
            case SELECTED_MEMBERS -> !deleted && allowedUserIds.contains(userId);
            case HIDDEN      -> false;
        };
    }

    @Override
    public String toString() {
        return "Post{id='" + id + "', authorId='" + authorId + "', visibility=" + visibility + "}";
    }
}
