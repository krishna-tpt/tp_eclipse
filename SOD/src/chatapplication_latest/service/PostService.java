package chatapplication_latest.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import chatapplication_latest.enums.PostVisibility;
import chatapplication_latest.exception.AuthException;
import chatapplication_latest.model.Post;
import chatapplication_latest.model.User;
import chatapplication_latest.repository.GroupRepository;
import chatapplication_latest.repository.PostRepository;

/**
 * Business logic for creating, reading, and moderating Posts.
 */
public class PostService {

    private final PostRepository  postRepo;
    private final GroupRepository groupRepo;
    private final AuthService     authService;

    public PostService(PostRepository postRepo,
                       GroupRepository groupRepo,
                       AuthService authService) {
        this.postRepo    = postRepo;
        this.groupRepo   = groupRepo;
        this.authService = authService;
    }

    // ── Any authenticated user ─────────────────────────────────────

    /**
     * Create a personal (feed) post.  Any authenticated user or admin.
     */
    public Post createFeedPost(String token, String content) {
        User author = authService.resolveSession(token);
        Post post   = new Post(author.getId(), content, null);
        postRepo.save(post);
        return post;
    }

    /**
     * Create a post inside a group.  Caller must be a group member.
     * Admins may create posts in any group.
     */
    public Post createGroupPost(String token, String groupId, String content) {
        User author = authService.resolveSession(token);
        var group   = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupId));

        if (!author.isAdmin() && !group.isMember(author.getId())) {
            throw new AuthException("Must be a member to post in this group");
        }

        Post post = new Post(author.getId(), content, groupId);
        group.addPost(post);
        postRepo.save(post);
        return post;
    }

    /**
     * Get all visible posts for a user in the global feed.
     */
    public List<Post> getFeedPosts(String token) {
        User viewer = authService.resolveSession(token);
        return postRepo.findAll().stream()
                .filter(p -> p.getGroupId() == null)
                .filter(p -> p.isVisibleTo(viewer.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Get all visible posts in a group for a member.
     */
    public List<Post> getGroupPosts(String token, String groupId) {
        User viewer = authService.resolveSession(token);
        var group   = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupId));

        if (!viewer.isAdmin() && !group.isMember(viewer.getId())) {
            throw new AuthException("Must be a member to view group posts");
        }

        return postRepo.findByGroupId(groupId).stream()
                .filter(p -> viewer.isAdmin() || p.isVisibleTo(viewer.getId()))
                .collect(Collectors.toList());
    }

    // ── Admin-only moderation ──────────────────────────────────────

    /**
     * Admin: hide a post (sets visibility to HIDDEN).
     */
    public void hidePost(String adminToken, String postId) {
        authService.resolveAdminSession(adminToken);
        Post post = getPostOrThrow(postId);
        post.setVisibility(PostVisibility.HIDDEN);
        postRepo.save(post);
    }

    /**
     * Admin: unhide a post (restore to PUBLIC or GROUP_ONLY).
     */
    public void unhidePost(String adminToken, String postId) {
        authService.resolveAdminSession(adminToken);
        Post post = getPostOrThrow(postId);
        PostVisibility restored = post.getGroupId() != null
                ? PostVisibility.GROUP_ONLY
                : PostVisibility.PUBLIC;
        post.setVisibility(restored);
        postRepo.save(post);
    }

    /**
     * Admin: share a group post with specific users only (SELECTED_MEMBERS).
     * Mirrors Slack/Teams "share with specific people" feature.
     */
    public void shareWithSelectedMembers(String adminToken, String postId, Set<String> targetUserIds) {
        authService.resolveAdminSession(adminToken);
        Post post = getPostOrThrow(postId);

        if (post.getGroupId() == null) {
            throw new RuntimeException("SELECTED_MEMBERS visibility is only for group posts");
        }

        post.setVisibility(PostVisibility.SELECTED_MEMBERS);
        targetUserIds.forEach(post::addAllowedUser);
        postRepo.save(post);
    }

    /**
     * Admin: restore group post to visible to all group members.
     */
    public void makeGroupPost(String adminToken, String postId) {
        authService.resolveAdminSession(adminToken);
        Post post = getPostOrThrow(postId);
        post.setVisibility(PostVisibility.GROUP_ONLY);
        postRepo.save(post);
    }

    // ── Helpers ────────────────────────────────────────────────────

    private Post getPostOrThrow(String postId) {
        return postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found: " + postId));
    }
}
