package chatapplication_latest.service;

import java.util.List;

import chatapplication_latest.model.Comment;
import chatapplication_latest.model.Post;
import chatapplication_latest.model.User;
import chatapplication_latest.repository.CommentRepository;
import chatapplication_latest.repository.PostRepository;

/**
 * Business logic for creating and moderating Comments.
 * Each post supports multiple comments (1-to-many).
 */
public class CommentService {

    private final CommentRepository commentRepo;
    private final PostRepository    postRepo;
    private final AuthService       authService;

    public CommentService(CommentRepository commentRepo,
                          PostRepository postRepo,
                          AuthService authService) {
        this.commentRepo = commentRepo;
        this.postRepo    = postRepo;
        this.authService = authService;
    }

    // ── Any authenticated user ─────────────────────────────────────

    /**
     * Add a comment to a post.  Any authenticated user or admin can comment.
     */
    public Comment addComment(String token, String postId, String content) {
        User author = authService.resolveSession(token);
        Post post   = getPostOrThrow(postId);

        if (post.isDeleted()) {
            throw new RuntimeException("Cannot comment on a deleted post");
        }

        Comment comment = new Comment(postId, author.getId(), content);
        post.addComment(comment);
        commentRepo.save(comment);
        return comment;
    }

    /**
     * Get all visible comments for a post.
     * Non-admin users only see VISIBLE comments.
     * Admin users see all comments (for moderation context).
     */
    public List<Comment> getComments(String token, String postId) {
        User viewer = authService.resolveSession(token);
        getPostOrThrow(postId);   // verify post exists

        if (viewer.isAdmin()) {
            return commentRepo.findByPostId(postId);
        } else {
            return commentRepo.findVisibleByPostId(postId);
        }
    }

    // ── Admin-only moderation ──────────────────────────────────────

    /**
     * Admin: hide a user's comment (it remains in the DB but is not shown to others).
     */
    public void hideComment(String adminToken, String commentId) {
        authService.resolveAdminSession(adminToken);
        Comment comment = getCommentOrThrow(commentId);
        comment.hide();
        commentRepo.save(comment);
    }

    /**
     * Admin: unhide a previously hidden comment.
     */
    public void unhideComment(String adminToken, String commentId) {
        authService.resolveAdminSession(adminToken);
        Comment comment = getCommentOrThrow(commentId);
        comment.unhide();
        commentRepo.save(comment);
    }

    // ── Helpers ────────────────────────────────────────────────────

    private Post getPostOrThrow(String postId) {
        return postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found: " + postId));
    }

    private Comment getCommentOrThrow(String commentId) {
        return commentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found: " + commentId));
    }
}
