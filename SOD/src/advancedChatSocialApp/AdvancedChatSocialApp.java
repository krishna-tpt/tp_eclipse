package advancedChatSocialApp;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

// ====================== BASE USER ======================
abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String userId;
    protected String username;
    protected String email;
    protected String passwordHash;

    public User(String username, String email, String password) {
        this.userId = "U" + UUID.randomUUID().toString().substring(0, 8);
        this.username = username;
        this.email = email.toLowerCase();
        this.passwordHash = hashPassword(password);
    }

    private String hashPassword(String password) {
        return String.valueOf(password.hashCode()); // Simple for demo (use BCrypt in production)
    }

    public boolean authenticate(String password) {
        return this.passwordHash.equals(hashPassword(password));
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public abstract boolean isAdmin();
}

// ====================== ADMIN (Inheritance) ======================
class Admin extends User implements Serializable {
    private static final long serialVersionUID = 1L;

    public Admin(String username, String email, String password) {
        super(username, email, password);
    }

    @Override
    public boolean isAdmin() {
        return true;
    }
}

// ====================== NORMAL USER ======================
class NormalUser extends User implements Serializable {
    private static final long serialVersionUID = 1L;

    public NormalUser(String username, String email, String password) {
        super(username, email, password);
    }

    @Override
    public boolean isAdmin() {
        return false;
    }
}

// ====================== CONTENT CLASSES ======================
class Post implements Serializable {
    private static final long serialVersionUID = 1L;
    private String postId;
    private String authorId;
    private String content;
    private LocalDateTime timestamp;
    private List<Comment> comments = new ArrayList<>();
    private boolean isHidden = false;
    private Set<String> visibleToGroups = new HashSet<>();

    public Post(String authorId, String content) {
        this.postId = "P" + UUID.randomUUID().toString().substring(0, 8);
        this.authorId = authorId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public void addComment(Comment comment) { comments.add(comment); }
    public void hide() { isHidden = true; }
    public void unhide() { isHidden = false; }
    public boolean isHidden() { return isHidden; }
    public void shareInGroup(String groupId) { visibleToGroups.add(groupId); }

    public String getPostId() { return postId; }
    public String getAuthorId() { return authorId; }
    public String getContent() { return content; }
    public List<Comment> getComments() { return new ArrayList<>(comments); }
    public boolean isVisibleTo(String groupId) {
        return visibleToGroups.isEmpty() || visibleToGroups.contains(groupId);
    }
}

class Comment implements Serializable {
    private static final long serialVersionUID = 1L;
    private String commentId;
    private String authorId;
    private String content;
    private LocalDateTime timestamp;
    private boolean isHidden = false;

    public Comment(String authorId, String content) {
        this.commentId = "C" + UUID.randomUUID().toString().substring(0, 8);
        this.authorId = authorId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public void hide() { isHidden = true; }
    public void unhide() { isHidden = false; }
    public boolean isHidden() { return isHidden; }

    public String getCommentId() { return commentId; }
    public String getAuthorId() { return authorId; }
    public String getContent() { return content; }
}

class GroupChat implements Serializable {
    private static final long serialVersionUID = 1L;
    private String groupId;
    private String groupName;
    private String adminId;
    private Set<String> members = new HashSet<>();
    private List<Post> messages = new ArrayList<>();
    private boolean isHidden = false;

    public GroupChat(String groupName, String adminId) {
        this.groupId = "G" + UUID.randomUUID().toString().substring(0, 8);
        this.groupName = groupName;
        this.adminId = adminId;
        members.add(adminId);
    }

    public void addMember(String userId) { members.add(userId); }
    public void hideGroup() { isHidden = true; }
    public void unhideGroup() { isHidden = false; }
    public boolean isHidden() { return isHidden; }

    public void postMessage(Post post) {
        if (!isHidden) messages.add(post);
    }

    public String getGroupId() { return groupId; }
    public String getGroupName() { return groupName; }
    public Set<String> getMembers() { return new HashSet<>(members); }
    public List<Post> getMessages() { return new ArrayList<>(messages); }
}

// ====================== VALIDATION ======================
class Validator {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}

// ====================== SERVICES ======================
class AuthService {
    private Map<String, User> usersByEmail = new HashMap<>();
    private Map<String, User> usersByUsername = new HashMap<>();

    public boolean signUp(String username, String email, String password, boolean isAdmin) {
        if (!Validator.isValidEmail(email)) {
            System.out.println("❌ Invalid email format!");
            return false;
        }
        if (!Validator.isStrongPassword(password)) {
            System.out.println("❌ Weak password! Must be 8+ chars with uppercase, lowercase, number & special char.");
            return false;
        }
        if (usersByEmail.containsKey(email) || usersByUsername.containsKey(username)) {
            System.out.println("❌ Username or Email already exists!");
            return false;
        }

        User user = isAdmin ? new Admin(username, email, password) : new NormalUser(username, email, password);
        usersByEmail.put(email, user);
        usersByUsername.put(username, user);
        System.out.println("✅ " + (isAdmin ? "Admin" : "User") + " registered successfully!");
        return true;
    }

    public User signIn(String usernameOrEmail, String password) {
        User user = usersByEmail.get(usernameOrEmail);
        if (user == null) user = usersByUsername.get(usernameOrEmail);

        if (user != null && user.authenticate(password)) {
            System.out.println("✅ Login successful! Welcome " + user.getUsername());
            return user;
        }
        System.out.println("❌ Invalid credentials!");
        return null;
    }

    // Persistence
    public void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("users.ser"))) {
            oos.writeObject(usersByEmail);
        } catch (Exception e) {
            System.out.println("Error saving users");
        }
    }

    @SuppressWarnings("unchecked")
    public void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users.ser"))) {
            usersByEmail = (Map<String, User>) ois.readObject();
            usersByUsername.clear();
            for (User u : usersByEmail.values()) {
                usersByUsername.put(u.getUsername(), u);
            }
        } catch (Exception e) {
            // File not found is normal on first run
        }
    }
}

class ContentService {
    private Map<String, Post> allPosts = new HashMap<>();
    private Map<String, GroupChat> groups = new HashMap<>();

    public Post createPost(String authorId, String content) {
        Post post = new Post(authorId, content);
        allPosts.put(post.getPostId(), post);
        System.out.println("✅ Post created!");
        return post;
    }

    public Comment addComment(String postId, String authorId, String content) {
        Post post = allPosts.get(postId);
        if (post == null) {
            System.out.println("❌ Post not found!");
            return null;
        }
        Comment comment = new Comment(authorId, content);
        post.addComment(comment);
        System.out.println("✅ Comment added!");
        return comment;
    }

    public GroupChat createGroup(String groupName, String adminId) {
        GroupChat group = new GroupChat(groupName, adminId);
        groups.put(group.getGroupId(), group);
        System.out.println("✅ Group created: " + groupName);
        return group;
    }

    public void postInGroup(String groupId, Post post, String userId) {
        GroupChat group = groups.get(groupId);
        if (group != null && group.getMembers().contains(userId)) {
            group.postMessage(post);
            System.out.println("✅ Message posted in group!");
        }
    }

    public void hideComment(String postId, String commentId, boolean hide) {
        Post post = allPosts.get(postId);
        if (post == null) return;
        for (Comment c : post.getComments()) {
            if (c.getCommentId().equals(commentId)) {
                if (hide) c.hide(); else c.unhide();
                System.out.println("✅ Comment " + (hide ? "hidden" : "unhidden"));
                return;
            }
        }
    }

    public void hideGroup(String groupId, boolean hide) {
        GroupChat group = groups.get(groupId);
        if (group != null) {
            if (hide) group.hideGroup(); else group.unhideGroup();
            System.out.println("✅ Group " + (hide ? "hidden" : "unhidden"));
        }
    }

    public void sharePostInGroup(String postId, String groupId) {
        Post post = allPosts.get(postId);
        if (post != null) {
            post.shareInGroup(groupId);
            System.out.println("✅ Post shared in group!");
        }
    }

    public void viewFeed() {
        System.out.println("\n=== 🌐 Global Feed ===");
        allPosts.values().stream()
                .filter(p -> !p.isHidden())
                .forEach(p -> {
                    System.out.println("[" + p.getPostId() + "] " + p.getContent());
                    p.getComments().forEach(c -> {
                        if (!c.isHidden()) {
                            System.out.println("   └─ 💬 " + c.getContent());
                        }
                    });
                });
    }

    // Persistence
    public void saveData() {
        saveObject(allPosts, "posts.ser");
        saveObject(groups, "groups.ser");
    }

    @SuppressWarnings("unchecked")
    public void loadData() {
        allPosts = (Map<String, Post>) loadObject("posts.ser");
        groups = (Map<String, GroupChat>) loadObject("groups.ser");
        if (allPosts == null) allPosts = new HashMap<>();
        if (groups == null) groups = new HashMap<>();
    }

    private void saveObject(Object obj, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(obj);
        } catch (Exception e) {}
    }

    private Object loadObject(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }
}

// ====================== REAL-TIME THREAD ======================
class FeedRefresher extends Thread {
    private ContentService contentService;
    private volatile boolean running = true;

    public FeedRefresher(ContentService cs) {
        this.contentService = cs;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(5000); // Refresh every 5 seconds
                System.out.println("\n🔄 [Auto-refresh] New updates available...");
                contentService.viewFeed();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stopRefresher() {
        running = false;
    }
}

// ====================== MAIN APPLICATION ======================
public class AdvancedChatSocialApp {
    public static void main(String[] args) {
        AuthService auth = new AuthService();
        ContentService content = new ContentService();
        Scanner sc = new Scanner(System.in);

        // Load persisted data
        auth.loadUsers();
        content.loadData();

        User currentUser = null;
        FeedRefresher refresher = null;

        while (true) {
            System.out.println("\n=== 🚀 Advanced Chat & Social App ===");
            System.out.println("1. Sign Up (User)");
            System.out.println("2. Sign Up (Admin)");
            System.out.println("3. Sign In");
            System.out.println("4. Create Post");
            System.out.println("5. Add Comment");
            System.out.println("6. Create Group");
            System.out.println("7. Post in Group");
            System.out.println("8. View Feed");
            System.out.println("9. Admin Actions");
            System.out.println("10. Logout");
            System.out.println("0. Exit");
            System.out.print("Choose option: ");

            int choice = getIntInput(sc);

            switch (choice) {
                case 1: // User Sign Up
                    System.out.print("Username: ");
                    String u = sc.nextLine();
                    System.out.print("Email: ");
                    String e = sc.nextLine();
                    System.out.print("Password: ");
                    String p = sc.nextLine();
                    auth.signUp(u, e, p, false);
                    break;

                case 2: // Admin Sign Up
                    System.out.print("Admin Username: ");
                    String ua = sc.nextLine();
                    System.out.print("Email: ");
                    String ea = sc.nextLine();
                    System.out.print("Password: ");
                    String pa = sc.nextLine();
                    auth.signUp(ua, ea, pa, true);
                    break;

                case 3: // Sign In
                    System.out.print("Username/Email: ");
                    String login = sc.nextLine();
                    System.out.print("Password: ");
                    String pass = sc.nextLine();
                    currentUser = auth.signIn(login, pass);

                    if (currentUser != null && refresher == null) {
                        refresher = new FeedRefresher(content);
                        refresher.start();
                    }
                    break;

                case 4: // Create Post
                    if (currentUser == null) { System.out.println("❌ Please login first!"); break; }
                    System.out.print("Post content: ");
                    content.createPost(currentUser.getUserId(), sc.nextLine());
                    break;

                case 5: // Add Comment
                    if (currentUser == null) { System.out.println("❌ Please login first!"); break; }
                    System.out.print("Post ID: ");
                    String pid = sc.nextLine();
                    System.out.print("Comment: ");
                    content.addComment(pid, currentUser.getUserId(), sc.nextLine());
                    break;

                case 6: // Create Group
                    if (currentUser == null) { System.out.println("❌ Please login first!"); break; }
                    System.out.print("Group Name: ");
                    content.createGroup(sc.nextLine(), currentUser.getUserId());
                    break;

                case 7: // Post in Group
                    if (currentUser == null) { System.out.println("❌ Please login first!"); break; }
                    System.out.print("Group ID: ");
                    String gid = sc.nextLine();
                    System.out.print("Message: ");
                    Post gp = new Post(currentUser.getUserId(), sc.nextLine());
                    content.postInGroup(gid, gp, currentUser.getUserId());
                    break;

                case 8:
                    content.viewFeed();
                    break;

                case 9: // Admin Actions
                    if (currentUser == null || !currentUser.isAdmin()) {
                        System.out.println("❌ Admin access only!");
                        break;
                    }
                    System.out.println("1. Hide Group  2. Unhide Group  3. Hide Comment  4. Share Post in Group");
                    int ac = getIntInput(sc);
                    if (ac == 1) {
                        System.out.print("Group ID: ");
                        content.hideGroup(sc.nextLine(), true);
                    } else if (ac == 2) {
                        System.out.print("Group ID: ");
                        content.hideGroup(sc.nextLine(), false);
                    } else if (ac == 3) {
                        System.out.print("Post ID: ");
                        String poid = sc.nextLine();
                        System.out.print("Comment ID: ");
                        String cid = sc.nextLine();
                        content.hideComment(poid, cid, true);
                    } else if (ac == 4) {
                        System.out.print("Post ID: ");
                        String po = sc.nextLine();
                        System.out.print("Group ID: ");
                        content.sharePostInGroup(po, sc.nextLine());
                    }
                    break;

                case 10:
                    if (currentUser != null) {
                        currentUser = null;
                        if (refresher != null) {
                            refresher.stopRefresher();
                            refresher = null;
                        }
                        System.out.println("✅ Logged out.");
                    }
                    break;

                case 0:
                    auth.saveUsers();
                    content.saveData();
                    System.out.println("👋 Exiting... Data saved.");
                    if (refresher != null) refresher.stopRefresher();
                    sc.close();
                    return;
            }
        }
    }

    private static int getIntInput(Scanner sc) {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.print("Invalid input! Enter number: ");
            }
        }
    }
}