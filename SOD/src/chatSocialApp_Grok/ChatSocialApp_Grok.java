package chatSocialApp_Grok;

import java.util.*;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

// ====================== ENTITY CLASSES ======================

class User {
	private String userId;
	private String username;
	private String email;
	private String passwordHash; // Simple hash for demo
	private boolean isAdmin;
	private Set<String> groupsJoined = new HashSet<>();

	public User(String username, String email, String password, boolean isAdmin) {
		this.userId = "U" + UUID.randomUUID().toString().substring(0, 8);
		this.username = username;
		this.email = email;
		this.passwordHash = String.valueOf(password.hashCode());
		this.isAdmin = isAdmin;
	}

	public boolean authenticate(String password) {
		return this.passwordHash.equals(String.valueOf(password.hashCode()));
	}

	// Getters & Setters
	public String getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public Set<String> getGroupsJoined() {
		return new HashSet<>(groupsJoined);
	}

	public void joinGroup(String groupId) {
		groupsJoined.add(groupId);
	}
}

// ====================== CONTENT CLASSES ======================

class Post {
	private String postId;
	private String authorId;
	private String content;
	private LocalDateTime timestamp;
	private List<Comment> comments = new ArrayList<>();
	private boolean isHidden = false; // Admin can hide post
	private Set<String> visibleToGroups = new HashSet<>(); // For shared in specific groups

	public Post(String authorId, String content) {
		this.postId = "P" + UUID.randomUUID().toString().substring(0, 8);
		this.authorId = authorId;
		this.content = content;
		this.timestamp = LocalDateTime.now();
	}

	public void addComment(Comment comment) {
		comments.add(comment);
	}

	public void hide() {
		this.isHidden = true;
	}

	public void unhide() {
		this.isHidden = false;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void shareInGroup(String groupId) {
		visibleToGroups.add(groupId);
	}

	// Getters
	public String getPostId() {
		return postId;
	}

	public String getAuthorId() {
		return authorId;
	}

	public String getContent() {
		return content;
	}

	public List<Comment> getComments() {
		return new ArrayList<>(comments);
	}

	public boolean isVisibleTo(String groupId) {
		return visibleToGroups.isEmpty() || visibleToGroups.contains(groupId);
	}
}

class Comment {
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

	public void hide() {
		this.isHidden = true;
	}

	public void unhide() {
		this.isHidden = false;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public String getCommentId() {
		return commentId;
	}

	public String getAuthorId() {
		return authorId;
	}

	public String getContent() {
		return content;
	}
}

// ====================== GROUP CHAT ======================

class GroupChat {
	private String groupId;
	private String groupName;
	private String adminId;
	private Set<String> members = new HashSet<>();
	private List<Post> messages = new ArrayList<>(); // Using Post for group messages too
	private boolean isHidden = false;

	public GroupChat(String groupName, String adminId) {
		this.groupId = "G" + UUID.randomUUID().toString().substring(0, 8);
		this.groupName = groupName;
		this.adminId = adminId;
		members.add(adminId);
	}

	public void addMember(String userId) {
		members.add(userId);
	}

	public void hideGroup() {
		this.isHidden = true;
	}

	public void unhideGroup() {
		this.isHidden = false;
	}

	public boolean isGroupHidden() {
		return isHidden;
	}

	public void postMessage(Post post) {
		if (!isHidden) {
			messages.add(post);
		}
	}

	public List<Post> getMessages() {
		return new ArrayList<>(messages);
	}

	public String getGroupId() {
		return groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public Set<String> getMembers() {
		return new HashSet<>(members);
	}
}

// ====================== SERVICE LAYER ======================

class AuthService {
	private Map<String, User> usersByEmail = new HashMap<>();
	private Map<String, User> usersByUsername = new HashMap<>();

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

	public boolean signUp(String username, String email, String password, boolean isAdmin) {
		if (!EMAIL_PATTERN.matcher(email).matches()) {
			System.out.println("Invalid email format!");
			return false;
		}
		if (usersByEmail.containsKey(email) || usersByUsername.containsKey(username)) {
			System.out.println("Username or Email already exists!");
			return false;
		}

		User user = new User(username, email, password, isAdmin);
		usersByEmail.put(email, user);
		usersByUsername.put(username, user);
		System.out.println((isAdmin ? "Admin" : "User") + " registered successfully: " + username);
		return true;
	}

	public User signIn(String usernameOrEmail, String password) {
		User user = usersByEmail.get(usernameOrEmail);
		if (user == null) {
			user = usersByUsername.get(usernameOrEmail);
		}
		if (user != null && user.authenticate(password)) {
			System.out.println("Login successful! Welcome " + user.getUsername());
			return user;
		}
		System.out.println("Invalid credentials!");
		return null;
	}
}

class ContentService {
	private Map<String, Post> allPosts = new HashMap<>();
	private Map<String, GroupChat> groups = new HashMap<>();

	public Post createPost(String authorId, String content) {
		Post post = new Post(authorId, content);
		allPosts.put(post.getPostId(), post);
		System.out.println("Post created successfully!");
		return post;
	}

	public Comment addComment(String postId, String authorId, String content) {
		Post post = allPosts.get(postId);
		if (post == null) {
			System.out.println("Post not found!");
			return null;
		}
		Comment comment = new Comment(authorId, content);
		post.addComment(comment);
		System.out.println("Comment added!");
		return comment;
	}

	public void hideComment(String postId, String commentId, boolean hide) {
		Post post = allPosts.get(postId);
		if (post == null)
			return;
		for (Comment c : post.getComments()) {
			if (c.getCommentId().equals(commentId)) {
				if (hide)
					c.hide();
				else
					c.unhide();
				System.out.println("Comment " + (hide ? "hidden" : "unhidden"));
				return;
			}
		}
	}

	public GroupChat createGroup(String groupName, String adminId) {
		GroupChat group = new GroupChat(groupName, adminId);
		groups.put(group.getGroupId(), group);
		System.out.println("Group created: " + groupName);
		return group;
	}

	public void postInGroup(String groupId, Post post, String userId) {
		GroupChat group = groups.get(groupId);
		if (group != null && group.getMembers().contains(userId)) {
			group.postMessage(post);
			System.out.println("Message posted in group " + group.getGroupName());
		}
	}

	public void hideGroup(String groupId, boolean hide) {
		GroupChat group = groups.get(groupId);
		if (group != null) {
			if (hide)
				group.hideGroup();
			else
				group.unhideGroup();
			System.out.println("Group " + (hide ? "hidden" : "unhidden"));
		}
	}

	public void sharePostInGroup(String postId, String groupId) {
		Post post = allPosts.get(postId);
		GroupChat group = groups.get(groupId);
		if (post != null && group != null) {
			post.shareInGroup(groupId);
			System.out.println("Post shared in group: " + group.getGroupName());
		}
	}

	public void viewFeed() {
		System.out.println("\n=== Global Feed ===");
		allPosts.values().forEach(p -> {
			if (!p.isHidden()) {
				System.out.println("Post [" + p.getPostId() + "]: " + p.getContent());
				p.getComments().forEach(c -> {
					if (!c.isHidden()) {
						System.out.println("   └─ Comment: " + c.getContent());
					}
				});
			}
		});
	}
}

// ====================== MAIN APPLICATION ======================

public class ChatSocialApp_Grok {
	public static void main(String[] args) {
		AuthService auth = new AuthService();
		ContentService content = new ContentService();
		Scanner sc = new Scanner(System.in);

		User currentUser = null;

		while (true) {
			System.out.println("\n=== Main Menu ===");
			System.out.println("1. Sign Up (User)");
			System.out.println("2. Sign Up (Admin)");
			System.out.println("3. Sign In");
			System.out.println("4. Create Post");
			System.out.println("5. Add Comment");
			System.out.println("6. Create Group");
			System.out.println("7. Post in Group");
			System.out.println("8. View Feed");
			System.out.println("9. Admin Actions (Hide/Unhide)");
			System.out.println("10. Logout");
			System.out.println("0. Exit");
			System.out.print("Choose: ");

			int choice = sc.nextInt();
			sc.nextLine(); // consume newline

			switch (choice) {
			case 1: // Sign Up User
				System.out.print("Username: ");
				String u1 = sc.nextLine();
				System.out.print("Email: ");
				String e1 = sc.nextLine();
				System.out.print("Password: ");
				String p1 = sc.nextLine();
				auth.signUp(u1, e1, p1, false);
				break;

			case 2: // Sign Up Admin
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
				break;

			case 4: // Create Post
				if (currentUser == null) {
					System.out.println("Please login first!");
					break;
				}
				System.out.print("Post content: ");
				String contentText = sc.nextLine();
				content.createPost(currentUser.getUserId(), contentText);
				break;

			case 5: // Add Comment
				if (currentUser == null) {
					System.out.println("Please login first!");
					break;
				}
				System.out.print("Post ID: ");
				String postId = sc.nextLine();
				System.out.print("Comment: ");
				String comm = sc.nextLine();
				content.addComment(postId, currentUser.getUserId(), comm);
				break;

			case 6: // Create Group
				if (currentUser == null) {
					System.out.println("Please login first!");
					break;
				}
				System.out.print("Group Name: ");
				String gname = sc.nextLine();
				content.createGroup(gname, currentUser.getUserId());
				break;

			case 7: // Post in Group
				if (currentUser == null) {
					System.out.println("Please login first!");
					break;
				}
				System.out.print("Group ID: ");
				String gid = sc.nextLine();
				System.out.print("Message: ");
				String msg = sc.nextLine();
				Post groupPost = new Post(currentUser.getUserId(), msg);
				content.postInGroup(gid, groupPost, currentUser.getUserId());
				break;

			case 8:
				content.viewFeed();
				break;

			case 9: // Admin Actions
				if (currentUser == null || !currentUser.isAdmin()) {
					System.out.println("Admin access only!");
					break;
				}
				System.out.println("1. Hide Group   2. Unhide Group   3. Hide Comment   4. Share Post in Group");
				int adminChoice = sc.nextInt();
				sc.nextLine();
				if (adminChoice == 1) {
					System.out.print("Group ID to hide: ");
					content.hideGroup(sc.nextLine(), true);
				} else if (adminChoice == 2) {
					System.out.print("Group ID to unhide: ");
					content.hideGroup(sc.nextLine(), false);
				} else if (adminChoice == 3) {
					System.out.print("Post ID: ");
					String pid = sc.nextLine();
					System.out.print("Comment ID: ");
					String cid = sc.nextLine();
					content.hideComment(pid, cid, true);
				} else if (adminChoice == 4) {
					System.out.print("Post ID: ");
					String pid2 = sc.nextLine();
					System.out.print("Group ID: ");
					String gid2 = sc.nextLine();
					content.sharePostInGroup(pid2, gid2);
				}
				break;

			case 10:
				currentUser = null;
				System.out.println("Logged out.");
				break;

			case 0:
				System.out.println("Exiting...");
				sc.close();
				return;
			}
		}
	}
}