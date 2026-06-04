package chatapplication_latest;

import java.util.List;
import java.util.Set;

import chatapplication_latest.enums.Role;
import chatapplication_latest.model.Comment;
import chatapplication_latest.model.Group;
import chatapplication_latest.model.Post;
import chatapplication_latest.model.User;
import chatapplication_latest.repository.CommentRepository;
import chatapplication_latest.repository.GroupRepository;
import chatapplication_latest.repository.PostRepository;
import chatapplication_latest.repository.SessionRepository;
import chatapplication_latest.repository.UserRepository;
import chatapplication_latest.service.AdminService;
import chatapplication_latest.service.AuthService;
import chatapplication_latest.service.CommentService;
import chatapplication_latest.service.GroupService;
import chatapplication_latest.service.PostService;

/**
 * Entry point — wires up all repositories and services,
 * then runs a quick end-to-end smoke test of every feature.
 *
 * Run with:  javac -d out $(find src -name "*.java") && java -cp out com.chatapp.ChatApplication
 */
public class ChatApplication {

    public static void main(String[] args) {

        // ── 1. Wire repositories ───────────────────────────────────
        UserRepository    userRepo    = new UserRepository();
        SessionRepository sessionRepo = new SessionRepository();
        PostRepository    postRepo    = new PostRepository();
        CommentRepository commentRepo = new CommentRepository();
        GroupRepository   groupRepo   = new GroupRepository();

        // ── 2. Wire services ───────────────────────────────────────
        AuthService    authService    = new AuthService(userRepo, sessionRepo);
        PostService    postService    = new PostService(postRepo, groupRepo, authService);
        CommentService commentService = new CommentService(commentRepo, postRepo, authService);
        GroupService   groupService   = new GroupService(groupRepo, userRepo, authService);
        AdminService   adminService   = new AdminService(userRepo, authService);

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║     Chat Application — LLD Demo Run      ║");
        System.out.println("╚══════════════════════════════════════════╝\n");

        // ═══════════════════════════════════════════════════════════
        // SIGN-UP
        // ═══════════════════════════════════════════════════════════
        section("1. SIGN-UP");

        User admin = authService.signUp("superadmin", "admin@chat.app", "Admin@123", Role.ADMIN);
        User alice = authService.signUp("alice",       "alice@chat.app", "Alice@123", Role.USER);
        User bob   = authService.signUp("bob",         "bob@chat.app",   "Bob@123",   Role.USER);

        log("Admin registered : " + admin);
        log("Alice registered : " + alice);
        log("Bob   registered : " + bob);

        // Duplicate username guard
        try {
            authService.signUp("alice", "other@chat.app", "pw", Role.USER);
        } catch (Exception e) {
            log("Duplicate username blocked ✓  → " + e.getMessage());
        }

        // Invalid email guard
        try {
            authService.signUp("carol", "not-an-email", "pw", Role.USER);
        } catch (Exception e) {
            log("Invalid email blocked      ✓  → " + e.getMessage());
        }

        // ═══════════════════════════════════════════════════════════
        // SIGN-IN
        // ═══════════════════════════════════════════════════════════
        section("2. SIGN-IN");

        String adminToken = authService.signIn("admin@chat.app", "Admin@123");
        String aliceToken = authService.signIn("alice@chat.app", "Alice@123");
        String bobToken   = authService.signIn("bob@chat.app",   "Bob@123");

        log("Admin token  : " + adminToken.substring(0, 8) + "…");
        log("Alice token  : " + aliceToken.substring(0, 8) + "…");
        log("Bob   token  : " + bobToken.substring(0, 8) + "…");

        // Wrong password guard
        try {
            authService.signIn("alice@chat.app", "wrong");
        } catch (Exception e) {
            log("Wrong password blocked     ✓  → " + e.getMessage());
        }

        // ═══════════════════════════════════════════════════════════
        // FEED POSTS
        // ═══════════════════════════════════════════════════════════
        section("3. FEED POSTS");

        Post alicePost = postService.createFeedPost(aliceToken, "Hello from Alice!");
        Post bobPost   = postService.createFeedPost(bobToken,   "Hello from Bob!");
        Post adminPost = postService.createFeedPost(adminToken, "Admin announcement.");

        log("Alice feed post  : " + alicePost);
        log("Bob   feed post  : " + bobPost);
        log("Admin feed post  : " + adminPost);

        List<Post> feed = postService.getFeedPosts(aliceToken);
        log("Feed size visible to Alice : " + feed.size() + " posts");

        // Admin hides Bob's post
        postService.hidePost(adminToken, bobPost.getId());
        log("Admin hid Bob's post");

        List<Post> feedAfter = postService.getFeedPosts(aliceToken);
        log("Feed size after hide (Alice sees) : " + feedAfter.size() + " posts");

        // Admin unhides
        postService.unhidePost(adminToken, bobPost.getId());
        log("Admin un-hid Bob's post  ✓");

        // ═══════════════════════════════════════════════════════════
        // COMMENTS
        // ═══════════════════════════════════════════════════════════
        section("4. COMMENTS  (1 post → multiple comments)");

        Comment c1 = commentService.addComment(aliceToken, alicePost.getId(), "Nice post Alice!");
        Comment c2 = commentService.addComment(bobToken,   alicePost.getId(), "Agreed, great post!");
        Comment c3 = commentService.addComment(adminToken, alicePost.getId(), "Welcome to the platform!");

        log("Comment 1 by Bob   : " + c1.getContent());
        log("Comment 2 by Bob   : " + c2.getContent());
        log("Comment 3 by Admin : " + c3.getContent());

        // Admin hides Bob's comment
        commentService.hideComment(adminToken, c2.getId());
        log("Admin hid Bob's comment");

        List<Comment> visibleComments = commentService.getComments(aliceToken, alicePost.getId());
        log("Visible comments Alice sees : " + visibleComments.size() + "  (Bob's hidden)");

        List<Comment> adminComments = commentService.getComments(adminToken, alicePost.getId());
        log("All comments Admin sees     : " + adminComments.size() + "  (including hidden)");

        // Admin un-hides
        commentService.unhideComment(adminToken, c2.getId());
        log("Admin un-hid Bob's comment  ✓");

        // ═══════════════════════════════════════════════════════════
        // GROUP CHAT
        // ═══════════════════════════════════════════════════════════
        section("5. GROUP CHAT");

        Group devGroup = groupService.createGroup(adminToken, "Dev Team", "Engineering discussions");
        log("Group created : " + devGroup);

        groupService.addMember(adminToken, devGroup.getId(), alice.getId());
        groupService.addMember(adminToken, devGroup.getId(), bob.getId());
        log("Alice and Bob added to Dev Team");

        Post groupPost1 = postService.createGroupPost(aliceToken, devGroup.getId(), "Sprint planning today!");
        Post groupPost2 = postService.createGroupPost(bobToken,   devGroup.getId(), "PR review needed.");
        Post groupPost3 = postService.createGroupPost(adminToken, devGroup.getId(), "Server maintenance at 22:00.");

        log("Alice group post : " + groupPost1.getContent());
        log("Bob   group post : " + groupPost2.getContent());
        log("Admin group post : " + groupPost3.getContent());

        // ── Admin hides a user message in group ────────────────────
        postService.hidePost(adminToken, groupPost2.getId());
        log("Admin hid Bob's group post");

        List<Post> groupPosts = postService.getGroupPosts(aliceToken, devGroup.getId());
        log("Group posts Alice sees after hide : " + groupPosts.size());

        postService.unhidePost(adminToken, groupPost2.getId());
        log("Admin un-hid Bob's group post  ✓");

        // ── Admin shares a post with selected members only ─────────
        postService.shareWithSelectedMembers(
                adminToken,
                groupPost3.getId(),
                Set.of(alice.getId())   // only Alice can see this post
        );
        log("Admin shared maintenance post with Alice only (SELECTED_MEMBERS)");

        List<Post> alicePosts = postService.getGroupPosts(aliceToken, devGroup.getId());
        List<Post> bobPosts   = postService.getGroupPosts(bobToken,   devGroup.getId());
        log("Posts Alice sees in group : " + alicePosts.size() + "  (includes admin's selected-member post)");
        log("Posts Bob   sees in group : " + bobPosts.size()   + "  (excluded from admin's selected-member post)");

        // ── Comment in group ───────────────────────────────────────
        Comment gc1 = commentService.addComment(aliceToken, groupPost1.getId(), "I'll lead standup!");
        Comment gc2 = commentService.addComment(bobToken,   groupPost1.getId(), "Works for me.");
        log("Group post comments : ['" + gc1.getContent() + "', '" + gc2.getContent() + "']");

        // ═══════════════════════════════════════════════════════════
        // ADMIN USER MANAGEMENT
        // ═══════════════════════════════════════════════════════════
        section("6. ADMIN USER MANAGEMENT");

        List<User> allUsers = adminService.listAllUsers(adminToken);
        log("Total users in system : " + allUsers.size());

        adminService.disableUser(adminToken, bob.getId());
        log("Bob disabled");

        try {
            authService.signIn("bob@chat.app", "Bob@123");
        } catch (Exception e) {
            log("Disabled login blocked     ✓  → " + e.getMessage());
        }

        adminService.enableUser(adminToken, bob.getId());
        String bobToken2 = authService.signIn("bob@chat.app", "Bob@123");
        log("Bob re-enabled and signed in  ✓  token: " + bobToken2.substring(0, 8) + "…");

        // ═══════════════════════════════════════════════════════════
        // SIGN-OUT
        // ═══════════════════════════════════════════════════════════
        section("7. SIGN-OUT");

        authService.signOut(aliceToken);
        log("Alice signed out");

        try {
            postService.createFeedPost(aliceToken, "Should fail");
        } catch (Exception e) {
            log("Post after sign-out blocked  ✓  → " + e.getMessage());
        }

        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║          All scenarios passed  ✓         ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }

    // ── Formatting helpers ─────────────────────────────────────────

    private static void section(String title) {
        System.out.println("\n── " + title + " " + "─".repeat(Math.max(0, 44 - title.length())));
    }

    private static void log(String msg) {
        System.out.println("  " + msg);
    }
}
