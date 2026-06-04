package chatapplication_latest.model;

import java.time.LocalDateTime;
import java.util.UUID;

import chatapplication_latest.enums.CommentStatus;

/**
 * Comment on a Post.  
 * A post may have multiple comments (1-to-many).
 * Admins can hide/unhide comments without deleting them.
 */
public class Comment {

    private final String id;
    private final String postId;
    private final String authorId;
    private String content;
    private CommentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Comment(String postId, String authorId, String content) {
        this.id        = UUID.randomUUID().toString();
        this.postId    = postId;
        this.authorId  = authorId;
        this.content   = content;
        this.status    = CommentStatus.VISIBLE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ── Getters ────────────────────────────────────────────────────
    public String        getId()        { return id; }
    public String        getPostId()    { return postId; }
    public String        getAuthorId()  { return authorId; }
    public String        getContent()   { return content; }
    public CommentStatus getStatus()    { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public boolean isVisible() { return status == CommentStatus.VISIBLE; }

    // ── Mutators ────────────────────────────────────────────────────
    public void setContent(String content) {
        this.content   = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void hide() {
        this.status    = CommentStatus.HIDDEN;
        this.updatedAt = LocalDateTime.now();
    }

    public void unhide() {
        this.status    = CommentStatus.VISIBLE;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Comment{id='" + id + "', postId='" + postId + "', status=" + status + "}";
    }
}
