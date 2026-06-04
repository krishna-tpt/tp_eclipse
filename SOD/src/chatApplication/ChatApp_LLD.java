package chatApplication;

//============================================================
//CHAT APPLICATION - LOW LEVEL DESIGN (Java)
//Features: Auth, Groups, Role-Based Access, Posts, Comments,
//        Hide/Unhide, Reactions, Notifications, DMs
//============================================================


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

//─────────────────────────────────────────────
//ENUMS
//─────────────────────────────────────────────

enum Role {
	SUPER_ADMIN, // Platform-wide admin
	GROUP_ADMIN, // Admin of a specific group
	MODERATOR, // Can hide/unhide posts & comments
	MEMBER, // Regular member
	GUEST // Read-only
}

enum PostStatus {
	VISIBLE, HIDDEN, DELETED, PINNED
}

enum CommentStatus {
	VISIBLE, HIDDEN, DELETED
}

enum NotificationType {
	NEW_POST, NEW_COMMENT, MENTION, REACTION, MEMBER_JOINED, POST_HIDDEN
}

enum ReactionType {
	LIKE, LOVE, HAHA, WOW, SAD, ANGRY
}

enum GroupVisibility {
	PUBLIC, PRIVATE, SECRET
}

//─────────────────────────────────────────────
//MODELS
//─────────────────────────────────────────────

class User {
	private final String userId;
	private String username;
	private String email;
	private String passwordHash;
	private String displayName;
	private String avatarUrl;
	private boolean isActive;
	private LocalDateTime createdAt;
	private LocalDateTime lastLoginAt;
	private Set<String> blockedUserIds;

	public User(String userId, String username, String email, String passwordHash) {
		this.userId = userId;
		this.username = username;
		this.email = email;
		this.passwordHash = passwordHash;
		this.isActive = true;
		this.createdAt = LocalDateTime.now();
		this.blockedUserIds = new HashSet<>();
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

	public String getPasswordHash() {
		return passwordHash;
	}

	public boolean isActive() {
		return isActive;
	}

	public LocalDateTime getLastLoginAt() {
		return lastLoginAt;
	}

	public Set<String> getBlockedUserIds() {
		return blockedUserIds;
	}

	public void setLastLoginAt(LocalDateTime t) {
		this.lastLoginAt = t;
	}

	public void setActive(boolean active) {
		this.isActive = active;
	}

	public void blockUser(String userId) {
		blockedUserIds.add(userId);
	}

	public void unblockUser(String userId) {
		blockedUserIds.remove(userId);
	}

	@Override
	public String toString() {
		return "User{id=" + userId + ", username=" + username + "}";
	}
}

class Group {
	private final String groupId;
	private String name;
	private String description;
	private GroupVisibility visibility;
	private String createdByUserId;
	private LocalDateTime createdAt;
	private Map<String, Role> memberRoles; // userId -> Role
	private List<Post> posts;
	private int maxMembers;
	private boolean isActive;

	public Group(String groupId, String name, String createdByUserId, GroupVisibility visibility) {
		this.groupId = groupId;
		this.name = name;
		this.createdByUserId = createdByUserId;
		this.visibility = visibility;
		this.createdAt = LocalDateTime.now();
		this.memberRoles = new ConcurrentHashMap<>();
		this.posts = new ArrayList<>();
		this.maxMembers = 1000;
		this.isActive = true;
		// Creator is Group Admin
		this.memberRoles.put(createdByUserId, Role.GROUP_ADMIN);
	}

	public String getGroupId() {
		return groupId;
	}

	public String getName() {
		return name;
	}

	public Map<String, Role> getMemberRoles() {
		return memberRoles;
	}

	public List<Post> getPosts() {
		return posts;
	}

	public GroupVisibility getVisibility() {
		return visibility;
	}

	public boolean isActive() {
		return isActive;
	}

	public boolean isMember(String userId) {
		return memberRoles.containsKey(userId);
	}

	public Role getMemberRole(String userId) {
		return memberRoles.getOrDefault(userId, null);
	}

	public void addMember(String userId, Role role) {
		if (memberRoles.size() < maxMembers) {
			memberRoles.put(userId, role);
		} else {
			throw new IllegalStateException("Group is full.");
		}
	}

	public void removeMember(String userId) {
		memberRoles.remove(userId);
	}

	public void addPost(Post post) {
		posts.add(0, post);
	} // newest first
}

class Post {
	private final String postId;
	private String authorId;
	private String groupId;
	private String content;
	private PostStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String hiddenByUserId;
	private String hideReason;
	private List<Comment> comments;
	private Map<ReactionType, Set<String>> reactions; // ReactionType -> Set<userId>
	private List<String> mediaUrls;
	private boolean isPinned;
	private List<String> mentionedUserIds;

	public Post(String postId, String authorId, String groupId, String content) {
		this.postId = postId;
		this.authorId = authorId;
		this.groupId = groupId;
		this.content = content;
		this.status = PostStatus.VISIBLE;
		this.createdAt = LocalDateTime.now();
		this.comments = new ArrayList<>();
		this.reactions = new EnumMap<>(ReactionType.class);
		this.mediaUrls = new ArrayList<>();
		this.mentionedUserIds = new ArrayList<>();
		for (ReactionType rt : ReactionType.values()) {
			reactions.put(rt, new HashSet<>());
		}
	}

	public String getPostId() {
		return postId;
	}

	public String getAuthorId() {
		return authorId;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getContent() {
		return content;
	}

	public PostStatus getStatus() {
		return status;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public Map<ReactionType, Set<String>> getReactions() {
		return reactions;
	}

	public boolean isPinned() {
		return isPinned;
	}

	public void hide(String moderatorId, String reason) {
		this.status = PostStatus.HIDDEN;
		this.hiddenByUserId = moderatorId;
		this.hideReason = reason;
		this.updatedAt = LocalDateTime.now();
	}

	public void unhide() {
		this.status = PostStatus.VISIBLE;
		this.hiddenByUserId = null;
		this.hideReason = null;
		this.updatedAt = LocalDateTime.now();
	}

	public void delete() {
		this.status = PostStatus.DELETED;
	}

	public void pin() {
		this.isPinned = true;
		this.status = PostStatus.PINNED;
	}

	public void unpin() {
		this.isPinned = false;
		this.status = PostStatus.VISIBLE;
	}

	public void addComment(Comment comment) {
		comments.add(comment);
	}

	public void addReaction(ReactionType type, String userId) {
		reactions.get(type).add(userId);
	}

	public void removeReaction(ReactionType type, String userId) {
		reactions.get(type).remove(userId);
	}

	public void setContent(String content) {
		this.content = content;
		this.updatedAt = LocalDateTime.now();
	}

	public void addMedia(String url) {
		mediaUrls.add(url);
	}
}

class Comment {
	private final String commentId;
	private String authorId;
	private String postId;
	private String content;
	private CommentStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String hiddenByUserId;
	private String parentCommentId; // for nested/threaded comments
	private Map<ReactionType, Set<String>> reactions;
	private List<String> mentionedUserIds;

	public Comment(String commentId, String authorId, String postId, String content, String parentCommentId) {
		this.commentId = commentId;
		this.authorId = authorId;
		this.postId = postId;
		this.content = content;
		this.parentCommentId = parentCommentId;
		this.status = CommentStatus.VISIBLE;
		this.createdAt = LocalDateTime.now();
		this.reactions = new EnumMap<>(ReactionType.class);
		this.mentionedUserIds = new ArrayList<>();
		for (ReactionType rt : ReactionType.values()) {
			reactions.put(rt, new HashSet<>());
		}
	}

	public String getCommentId() {
		return commentId;
	}

	public String getAuthorId() {
		return authorId;
	}

	public String getPostId() {
		return postId;
	}

	public String getContent() {
		return content;
	}

	public CommentStatus getStatus() {
		return status;
	}

	public String getParentCommentId() {
		return parentCommentId;
	}

	public void hide(String moderatorId) {
		this.status = CommentStatus.HIDDEN;
		this.hiddenByUserId = moderatorId;
		this.updatedAt = LocalDateTime.now();
	}

	public void unhide() {
		this.status = CommentStatus.VISIBLE;
		this.hiddenByUserId = null;
		this.updatedAt = LocalDateTime.now();
	}

	public void delete() {
		this.status = CommentStatus.DELETED;
	}

	public void setContent(String c) {
		this.content = c;
		this.updatedAt = LocalDateTime.now();
	}

	public void addReaction(ReactionType type, String userId) {
		reactions.get(type).add(userId);
	}
}

class DirectMessage {
	private final String dmId;
	private String senderId;
	private String receiverId;
	private String content;
	private LocalDateTime sentAt;
	private boolean isRead;
	private boolean isDeleted;

	public DirectMessage(String dmId, String senderId, String receiverId, String content) {
		this.dmId = dmId;
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.content = content;
		this.sentAt = LocalDateTime.now();
		this.isRead = false;
	}

	public String getDmId() {
		return dmId;
	}

	public String getSenderId() {
		return senderId;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public String getContent() {
		return content;
	}

	public boolean isRead() {
		return isRead;
	}

	public void markRead() {
		this.isRead = true;
	}

	public void delete() {
		this.isDeleted = true;
	}
}

class Notification {
	private final String notificationId;
	private String userId;
	private NotificationType type;
	private String message;
	private String referenceId; // postId / commentId / groupId
	private boolean isRead;
	private LocalDateTime createdAt;

	public Notification(String notificationId, String userId, NotificationType type, String message,
			String referenceId) {
		this.notificationId = notificationId;
		this.userId = userId;
		this.type = type;
		this.message = message;
		this.referenceId = referenceId;
		this.isRead = false;
		this.createdAt = LocalDateTime.now();
	}

	public String getNotificationId() {
		return notificationId;
	}

	public String getUserId() {
		return userId;
	}

	public boolean isRead() {
		return isRead;
	}

	public void markRead() {
		this.isRead = true;
	}
}

class Session {
	private final String sessionToken;
	private String userId;
	private LocalDateTime createdAt;
	private LocalDateTime expiresAt;

	public Session(String sessionToken, String userId, int durationMinutes) {
		this.sessionToken = sessionToken;
		this.userId = userId;
		this.createdAt = LocalDateTime.now();
		this.expiresAt = createdAt.plusMinutes(durationMinutes);
	}

	public String getSessionToken() {
		return sessionToken;
	}

	public String getUserId() {
		return userId;
	}

	public boolean isExpired() {
		return LocalDateTime.now().isAfter(expiresAt);
	}
}

//─────────────────────────────────────────────
//EXCEPTIONS
//─────────────────────────────────────────────

class UnauthorizedException extends RuntimeException {
	public UnauthorizedException(String msg) {
		super(msg);
	}
}

class UserNotFoundException extends RuntimeException {
	public UserNotFoundException(String msg) {
		super(msg);
	}
}

class GroupNotFoundException extends RuntimeException {
	public GroupNotFoundException(String msg) {
		super(msg);
	}
}

class PostNotFoundException extends RuntimeException {
	public PostNotFoundException(String msg) {
		super(msg);
	}
}

class DuplicateUserException extends RuntimeException {
	public DuplicateUserException(String msg) {
		super(msg);
	}
}

//─────────────────────────────────────────────
//INTERFACES
//─────────────────────────────────────────────

interface AuthService {
	User signUp(String username, String email, String password);

	Session signIn(String email, String password);

	void signOut(String sessionToken);

	User validateSession(String sessionToken);
}

interface GroupService {
	Group createGroup(String sessionToken, String name, String description, GroupVisibility visibility);

	void joinGroup(String sessionToken, String groupId);

	void leaveGroup(String sessionToken, String groupId);

	void addMember(String sessionToken, String groupId, String targetUserId, Role role);

	void removeMember(String sessionToken, String groupId, String targetUserId);

	void changeRole(String sessionToken, String groupId, String targetUserId, Role newRole);

	List<Group> searchGroups(String query);

	Group getGroupById(String groupId);
}

interface PostService {
	Post createPost(String sessionToken, String groupId, String content, List<String> mediaUrls);

	void editPost(String sessionToken, String postId, String newContent);

	void deletePost(String sessionToken, String postId);

	void hidePost(String sessionToken, String postId, String reason);

	void unhidePost(String sessionToken, String postId);

	void pinPost(String sessionToken, String postId);

	void unpinPost(String sessionToken, String postId);

	void reactToPost(String sessionToken, String postId, ReactionType type);

	List<Post> getGroupFeed(String sessionToken, String groupId, int page, int size);
}

interface CommentService {
	Comment addComment(String sessionToken, String postId, String content, String parentCommentId);

	void unhideComment(String sessionToken, String commentId, String reason);

	void editComment(String sessionToken, String commentId, String newContent);

	void deleteComment(String sessionToken, String commentId);

	void hideComment(String sessionToken, String commentId, String reason);

	void unhideComment(String sessionToken, String commentId);

	void reactToComment(String sessionToken, String commentId, ReactionType type);

	List<Comment> getComments(String sessionToken, String postId);
}

interface NotificationService {
	void sendNotification(String userId, NotificationType type, String message, String referenceId);

	List<Notification> getNotifications(String sessionToken);

	void markAsRead(String sessionToken, String notificationId);
}

interface DirectMessageService {
	DirectMessage sendDM(String sessionToken, String receiverId, String content);

	List<DirectMessage> getConversation(String sessionToken, String otherUserId);

	void markAsRead(String sessionToken, String dmId);
}

//─────────────────────────────────────────────
//HELPER UTILITIES
//─────────────────────────────────────────────

class IdGenerator {
	public static String generate(String prefix) {
		return prefix + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
	}
}

class PasswordUtil {
	public static String hash(String password) {
		// In real impl: BCrypt.hashpw(password, BCrypt.gensalt())
		return "hashed_" + password.hashCode();
	}

	public static boolean verify(String password, String hash) {
		return hash.equals(hash(password));
	}
}

class MentionParser {
	public static List<String> extractMentions(String content, Map<String, User> usersByUsername) {
		List<String> userIds = new ArrayList<>();
		String[] words = content.split("\\s+");
		for (String word : words) {
			if (word.startsWith("@")) {
				String username = word.substring(1);
				if (usersByUsername.containsKey(username)) {
					userIds.add(usersByUsername.get(username).getUserId());
				}
			}
		}
		return userIds;
	}
}

//─────────────────────────────────────────────
//RBAC (Role-Based Access Control)
//─────────────────────────────────────────────

class RBACService {
	/**
	 * Hierarchy: SUPER_ADMIN > GROUP_ADMIN > MODERATOR > MEMBER > GUEST
	 */
	public boolean canPost(Role role) {
		return role == Role.SUPER_ADMIN || role == Role.GROUP_ADMIN || role == Role.MODERATOR || role == Role.MEMBER;
	}

	public boolean canComment(Role role) {
		return canPost(role); // All members can comment
	}

	public boolean canHideUnhide(Role role) {
		return role == Role.SUPER_ADMIN || role == Role.GROUP_ADMIN || role == Role.MODERATOR;
	}

	public boolean canDeleteAnyPost(Role role) {
		return role == Role.SUPER_ADMIN || role == Role.GROUP_ADMIN;
	}

	public boolean canPinPost(Role role) {
		return role == Role.SUPER_ADMIN || role == Role.GROUP_ADMIN;
	}

	public boolean canManageMembers(Role role) {
		return role == Role.SUPER_ADMIN || role == Role.GROUP_ADMIN;
	}

	public boolean canEditPost(String requesterId, String authorId, Role role) {
		// Author can edit own post; admins can edit any
		return requesterId.equals(authorId) || role == Role.SUPER_ADMIN || role == Role.GROUP_ADMIN;
	}
}

//─────────────────────────────────────────────
//SERVICE IMPLEMENTATIONS
//─────────────────────────────────────────────

class AuthServiceImpl implements AuthService {
	private final Map<String, User> usersById = new ConcurrentHashMap<>();
	private final Map<String, User> usersByEmail = new ConcurrentHashMap<>();
	private final Map<String, User> usersByUsername = new ConcurrentHashMap<>();
	private final Map<String, Session> sessions = new ConcurrentHashMap<>();

	@Override
	public User signUp(String username, String email, String password) {
		if (usersByEmail.containsKey(email))
			throw new DuplicateUserException("Email already in use: " + email);
		if (usersByUsername.containsKey(username))
			throw new DuplicateUserException("Username already taken: " + username);

		String userId = IdGenerator.generate("usr");
		String passwordHash = PasswordUtil.hash(password);
		User user = new User(userId, username, email, passwordHash);

		usersById.put(userId, user);
		usersByEmail.put(email, user);
		usersByUsername.put(username, user);
		System.out.println("[AUTH] User registered: " + username);
		return user;
	}

	@Override
	public Session signIn(String email, String password) {
		User user = usersByEmail.get(email);
		if (user == null || !PasswordUtil.verify(password, user.getPasswordHash()))
			throw new UnauthorizedException("Invalid email or password.");
		if (!user.isActive())
			throw new UnauthorizedException("Account is deactivated.");

		String token = IdGenerator.generate("tok");
		Session session = new Session(token, user.getUserId(), 60 * 24); // 24h
		sessions.put(token, session);
		user.setLastLoginAt(LocalDateTime.now());
		System.out.println("[AUTH] User signed in: " + user.getUsername());
		return session;
	}

	@Override
	public void signOut(String sessionToken) {
		Session session = sessions.remove(sessionToken);
		if (session != null) {
			System.out.println("[AUTH] Session invalidated for userId: " + session.getUserId());
		}
	}

	@Override
	public User validateSession(String sessionToken) {
		Session session = sessions.get(sessionToken);
		if (session == null || session.isExpired()) {
			sessions.remove(sessionToken);
			throw new UnauthorizedException("Invalid or expired session.");
		}
		return usersById.get(session.getUserId());
	}

	public Map<String, User> getUsersByUsername() {
		return usersByUsername;
	}

	public User getUserById(String userId) {
		return usersById.get(userId);
	}
}

class GroupServiceImpl implements GroupService {
	private final Map<String, Group> groupsById = new ConcurrentHashMap<>();
	private final AuthServiceImpl authService;

	public GroupServiceImpl(AuthServiceImpl authService) {
		this.authService = authService;
	}

	@Override
	public Group createGroup(String sessionToken, String name, String description, GroupVisibility visibility) {
		User user = authService.validateSession(sessionToken);
		String groupId = IdGenerator.generate("grp");
		Group group = new Group(groupId, name, user.getUserId(), visibility);
		groupsById.put(groupId, group);
		System.out.println("[GROUP] Created group '" + name + "' by " + user.getUsername());
		return group;
	}

	@Override
	public void joinGroup(String sessionToken, String groupId) {
		User user = authService.validateSession(sessionToken);
		Group group = getGroupOrThrow(groupId);
		if (group.getVisibility() == GroupVisibility.SECRET)
			throw new UnauthorizedException("Cannot join secret group without invite.");
		group.addMember(user.getUserId(), Role.MEMBER);
		System.out.println("[GROUP] " + user.getUsername() + " joined " + group.getName());
	}

	@Override
	public void leaveGroup(String sessionToken, String groupId) {
		User user = authService.validateSession(sessionToken);
		Group group = getGroupOrThrow(groupId);
		group.removeMember(user.getUserId());
	}

	@Override
	public void addMember(String sessionToken, String groupId, String targetUserId, Role role) {
		User requester = authService.validateSession(sessionToken);
		Group group = getGroupOrThrow(groupId);
		Role requesterRole = group.getMemberRole(requester.getUserId());
		if (requesterRole == null || !new RBACService().canManageMembers(requesterRole))
			throw new UnauthorizedException("No permission to add members.");
		group.addMember(targetUserId, role);
	}

	@Override
	public void removeMember(String sessionToken, String groupId, String targetUserId) {
		User requester = authService.validateSession(sessionToken);
		Group group = getGroupOrThrow(groupId);
		Role requesterRole = group.getMemberRole(requester.getUserId());
		if (requesterRole == null || !new RBACService().canManageMembers(requesterRole))
			throw new UnauthorizedException("No permission to remove members.");
		group.removeMember(targetUserId);
	}

	@Override
	public void changeRole(String sessionToken, String groupId, String targetUserId, Role newRole) {
		User requester = authService.validateSession(sessionToken);
		Group group = getGroupOrThrow(groupId);
		Role requesterRole = group.getMemberRole(requester.getUserId());
		if (requesterRole != Role.SUPER_ADMIN && requesterRole != Role.GROUP_ADMIN)
			throw new UnauthorizedException("No permission to change roles.");
		group.getMemberRoles().put(targetUserId, newRole);
	}

	@Override
	public List<Group> searchGroups(String query) {
		return groupsById.values().stream().filter(g -> g.isActive() && g.getVisibility() != GroupVisibility.SECRET)
				.filter(g -> g.getName().toLowerCase().contains(query.toLowerCase())).collect(Collectors.toList());
	}

	@Override
	public Group getGroupById(String groupId) {
		return getGroupOrThrow(groupId);
	}

	private Group getGroupOrThrow(String groupId) {
		Group g = groupsById.get(groupId);
		if (g == null)
			throw new GroupNotFoundException("Group not found: " + groupId);
		return g;
	}

	public Map<String, Group> getGroupsById() {
		return groupsById;
	}
}

class PostServiceImpl implements PostService {
	private final Map<String, Post> postsById = new ConcurrentHashMap<>();
	private final AuthServiceImpl authService;
	private final GroupServiceImpl groupService;
	private final NotificationServiceImpl notificationService;
	private final RBACService rbac = new RBACService();

	public PostServiceImpl(AuthServiceImpl auth, GroupServiceImpl groupService,
			NotificationServiceImpl notificationService) {
		this.authService = auth;
		this.groupService = groupService;
		this.notificationService = notificationService;
	}

	@Override
	public Post createPost(String sessionToken, String groupId, String content, List<String> mediaUrls) {
		User user = authService.validateSession(sessionToken);
		Group group = groupService.getGroupById(groupId);

		Role role = group.getMemberRole(user.getUserId());
		if (role == null || !rbac.canPost(role))
			throw new UnauthorizedException("No permission to post in this group.");

		String postId = IdGenerator.generate("pst");
		Post post = new Post(postId, user.getUserId(), groupId, content);
		if (mediaUrls != null)
			mediaUrls.forEach(post::addMedia);
		postsById.put(postId, post);
		group.addPost(post);

		// Notify group members
		group.getMemberRoles().keySet().stream().filter(uid -> !uid.equals(user.getUserId()))
				.forEach(uid -> notificationService.sendNotification(uid, NotificationType.NEW_POST,
						user.getUsername() + " posted in " + group.getName(), postId));

		// Parse mentions
		MentionParser.extractMentions(content, authService.getUsersByUsername())
				.forEach(uid -> notificationService.sendNotification(uid, NotificationType.MENTION,
						"You were mentioned by " + user.getUsername(), postId));

		System.out.println("[POST] Created by " + user.getUsername() + " in " + group.getName());
		return post;
	}

	@Override
	public void editPost(String sessionToken, String postId, String newContent) {
		User user = authService.validateSession(sessionToken);
		Post post = getPostOrThrow(postId);
		Group group = groupService.getGroupById(post.getGroupId());
		Role role = group.getMemberRole(user.getUserId());
		if (!rbac.canEditPost(user.getUserId(), post.getAuthorId(), role))
			throw new UnauthorizedException("No permission to edit this post.");
		post.setContent(newContent);
	}

	@Override
	public void deletePost(String sessionToken, String postId) {
		User user = authService.validateSession(sessionToken);
		Post post = getPostOrThrow(postId);
		Group group = groupService.getGroupById(post.getGroupId());
		Role role = group.getMemberRole(user.getUserId());
		boolean isAuthor = user.getUserId().equals(post.getAuthorId());
		if (!isAuthor && !rbac.canDeleteAnyPost(role))
			throw new UnauthorizedException("No permission to delete this post.");
		post.delete();
	}

	@Override
	public void hidePost(String sessionToken, String postId, String reason) {
		User user = authService.validateSession(sessionToken);
		Post post = getPostOrThrow(postId);
		Group group = groupService.getGroupById(post.getGroupId());
		Role role = group.getMemberRole(user.getUserId());
		if (!rbac.canHideUnhide(role))
			throw new UnauthorizedException("No permission to hide posts.");
		post.hide(user.getUserId(), reason);
		notificationService.sendNotification(post.getAuthorId(), NotificationType.POST_HIDDEN,
				"Your post was hidden. Reason: " + reason, postId);
		System.out.println("[POST] Hidden by " + user.getUsername() + ": " + postId);
	}

	@Override
	public void unhidePost(String sessionToken, String postId) {
		User user = authService.validateSession(sessionToken);
		Post post = getPostOrThrow(postId);
		Group group = groupService.getGroupById(post.getGroupId());
		Role role = group.getMemberRole(user.getUserId());
		if (!rbac.canHideUnhide(role))
			throw new UnauthorizedException("No permission to unhide posts.");
		post.unhide();
		System.out.println("[POST] Unhidden by " + user.getUsername() + ": " + postId);
	}

	@Override
	public void pinPost(String sessionToken, String postId) {
		User user = authService.validateSession(sessionToken);
		Post post = getPostOrThrow(postId);
		Group group = groupService.getGroupById(post.getGroupId());
		Role role = group.getMemberRole(user.getUserId());
		if (!rbac.canPinPost(role))
			throw new UnauthorizedException("No permission to pin posts.");
		post.pin();
	}

	@Override
	public void unpinPost(String sessionToken, String postId) {
		User user = authService.validateSession(sessionToken);
		Post post = getPostOrThrow(postId);
		Group group = groupService.getGroupById(post.getGroupId());
		Role role = group.getMemberRole(user.getUserId());
		if (!rbac.canPinPost(role))
			throw new UnauthorizedException("No permission to unpin posts.");
		post.unpin();
	}

	@Override
	public void reactToPost(String sessionToken, String postId, ReactionType type) {
		User user = authService.validateSession(sessionToken);
		Post post = getPostOrThrow(postId);
		post.addReaction(type, user.getUserId());
		notificationService.sendNotification(post.getAuthorId(), NotificationType.REACTION,
				user.getUsername() + " reacted to your post.", postId);
	}

	@Override
	public List<Post> getGroupFeed(String sessionToken, String groupId, int page, int size) {
		User user = authService.validateSession(sessionToken);
		Group group = groupService.getGroupById(groupId);
		Role role = group.getMemberRole(user.getUserId());

		return group.getPosts().stream().filter(p -> p.getStatus() != PostStatus.DELETED).filter(p -> {
			// Non-moderators can't see hidden posts (except their own)
			if (p.getStatus() == PostStatus.HIDDEN) {
				return rbac.canHideUnhide(role != null ? role : Role.GUEST) || p.getAuthorId().equals(user.getUserId());
			}
			return true;
		}).skip((long) page * size).limit(size).collect(Collectors.toList());
	}

	private Post getPostOrThrow(String postId) {
		Post p = postsById.get(postId);
		if (p == null)
			throw new PostNotFoundException("Post not found: " + postId);
		return p;
	}

	public Post getPostById(String postId) {
		return postsById.get(postId);
	}
}

class CommentServiceImpl implements CommentService {
	private final Map<String, Comment> commentsById = new ConcurrentHashMap<>();
	private final AuthServiceImpl authService;
	private final PostServiceImpl postService;
	private final GroupServiceImpl groupService;
	private final NotificationServiceImpl notificationService;
	private final RBACService rbac = new RBACService();

	public CommentServiceImpl(AuthServiceImpl auth, PostServiceImpl postService, GroupServiceImpl groupService,
			NotificationServiceImpl notificationService) {
		this.authService = auth;
		this.postService = postService;
		this.groupService = groupService;
		this.notificationService = notificationService;
	}

	@Override
	public Comment addComment(String sessionToken, String postId, String content, String parentCommentId) {
		User user = authService.validateSession(sessionToken);
		Post post = postService.getPostById(postId);
		if (post == null)
			throw new PostNotFoundException("Post not found: " + postId);
		Group group = groupService.getGroupById(post.getGroupId());
		Role role = group.getMemberRole(user.getUserId());
		if (role == null || !rbac.canComment(role))
			throw new UnauthorizedException("No permission to comment.");

		String commentId = IdGenerator.generate("cmt");
		Comment comment = new Comment(commentId, user.getUserId(), postId, content, parentCommentId);
		commentsById.put(commentId, comment);
		post.addComment(comment);

		// Notify post author
		notificationService.sendNotification(post.getAuthorId(), NotificationType.NEW_COMMENT,
				user.getUsername() + " commented on your post.", postId);

		System.out.println("[COMMENT] Added by " + user.getUsername());
		return comment;
	}

	@Override
	public void editComment(String sessionToken, String commentId, String newContent) {
		User user = authService.validateSession(sessionToken);
		Comment comment = getCommentOrThrow(commentId);
		if (!comment.getAuthorId().equals(user.getUserId()))
			throw new UnauthorizedException("Can only edit your own comments.");
		comment.setContent(newContent);
	}

	@Override
	public void deleteComment(String sessionToken, String commentId) {
		User user = authService.validateSession(sessionToken);
		Comment comment = getCommentOrThrow(commentId);
		Post post = postService.getPostById(comment.getPostId());
		Group group = groupService.getGroupById(post.getGroupId());
		Role role = group.getMemberRole(user.getUserId());
		boolean isAuthor = user.getUserId().equals(comment.getAuthorId());
		if (!isAuthor && !rbac.canDeleteAnyPost(role))
			throw new UnauthorizedException("No permission to delete this comment.");
		comment.delete();
	}

	@Override
	public void hideComment(String sessionToken, String commentId, String reason) {
		User user = authService.validateSession(sessionToken);
		Comment comment = getCommentOrThrow(commentId);
		Post post = postService.getPostById(comment.getPostId());
		Group group = groupService.getGroupById(post.getGroupId());
		Role role = group.getMemberRole(user.getUserId());
		if (!rbac.canHideUnhide(role))
			throw new UnauthorizedException("No permission to hide comments.");
		comment.hide(user.getUserId());
		System.out.println("[COMMENT] Hidden by " + user.getUsername());
	}

	@Override
	public void unhideComment(String sessionToken, String commentId, String reason) {
		User user = authService.validateSession(sessionToken);
		Comment comment = getCommentOrThrow(commentId);
		Post post = postService.getPostById(comment.getPostId());
		Group group = groupService.getGroupById(post.getGroupId());
		Role role = group.getMemberRole(user.getUserId());
		if (!rbac.canHideUnhide(role))
			throw new UnauthorizedException("No permission to unhide comments.");
		comment.unhide();
	}

	@Override
	public void reactToComment(String sessionToken, String commentId, ReactionType type) {
		User user = authService.validateSession(sessionToken);
		Comment comment = getCommentOrThrow(commentId);
		comment.addReaction(type, user.getUserId());
	}

	@Override
	public List<Comment> getComments(String sessionToken, String postId) {
		User user = authService.validateSession(sessionToken);
		Post post = postService.getPostById(postId);
		Group group = groupService.getGroupById(post.getGroupId());
		Role role = group.getMemberRole(user.getUserId());

		return post.getComments().stream().filter(c -> c.getStatus() != CommentStatus.DELETED).filter(c -> {
			if (c.getStatus() == CommentStatus.HIDDEN) {
				return rbac.canHideUnhide(role != null ? role : Role.GUEST) || c.getAuthorId().equals(user.getUserId());
			}
			return true;
		}).collect(Collectors.toList());
	}

	private Comment getCommentOrThrow(String commentId) {
		Comment c = commentsById.get(commentId);
		if (c == null)
			throw new RuntimeException("Comment not found: " + commentId);
		return c;
	}

	@Override
	public void unhideComment(String sessionToken, String commentId) {
		// TODO Auto-generated method stub
		
	}
}

class NotificationServiceImpl implements NotificationService {
	private final Map<String, List<Notification>> notificationsByUserId = new ConcurrentHashMap<>();
	private final AuthServiceImpl authService;

	public NotificationServiceImpl(AuthServiceImpl authService) {
		this.authService = authService;
	}

	@Override
	public void sendNotification(String userId, NotificationType type, String message, String referenceId) {
		String notifId = IdGenerator.generate("ntf");
		Notification notif = new Notification(notifId, userId, type, message, referenceId);
		notificationsByUserId.computeIfAbsent(userId, k -> new ArrayList<>()).add(notif);
	}

	@Override
	public List<Notification> getNotifications(String sessionToken) {
		User user = authService.validateSession(sessionToken);
		return notificationsByUserId.getOrDefault(user.getUserId(), Collections.emptyList());
	}

	@Override
	public void markAsRead(String sessionToken, String notificationId) {
		User user = authService.validateSession(sessionToken);
		notificationsByUserId.getOrDefault(user.getUserId(), Collections.emptyList()).stream()
				.filter(n -> n.getNotificationId().equals(notificationId)).findFirst()
				.ifPresent(Notification::markRead);
	}
}

class DirectMessageServiceImpl implements DirectMessageService {
	private final Map<String, DirectMessage> dmsById = new ConcurrentHashMap<>();
	private final AuthServiceImpl authService;

	public DirectMessageServiceImpl(AuthServiceImpl authService) {
		this.authService = authService;
	}

	@Override
	public DirectMessage sendDM(String sessionToken, String receiverId, String content) {
		User sender = authService.validateSession(sessionToken);
		if (sender.getBlockedUserIds().contains(receiverId))
			throw new UnauthorizedException("You have blocked this user.");
		User receiver = authService.getUserById(receiverId);
		if (receiver == null)
			throw new UserNotFoundException("Receiver not found.");
		if (receiver.getBlockedUserIds().contains(sender.getUserId()))
			throw new UnauthorizedException("You cannot message this user.");

		String dmId = IdGenerator.generate("dm");
		DirectMessage dm = new DirectMessage(dmId, sender.getUserId(), receiverId, content);
		dmsById.put(dmId, dm);
		System.out.println("[DM] " + sender.getUsername() + " → " + receiver.getUsername());
		return dm;
	}

	@Override
	public List<DirectMessage> getConversation(String sessionToken, String otherUserId) {
		User user = authService.validateSession(sessionToken);
		return dmsById.values().stream().filter(dm -> !dm.isRead() || true) // include all
				.filter(dm -> (dm.getSenderId().equals(user.getUserId()) && dm.getReceiverId().equals(otherUserId))
						|| (dm.getSenderId().equals(otherUserId) && dm.getReceiverId().equals(user.getUserId())))
				.sorted(Comparator.comparing(dm -> dm.getClass().getName())) // stable sort
				.collect(Collectors.toList());
	}

	@Override
	public void markAsRead(String sessionToken, String dmId) {
		authService.validateSession(sessionToken);
		DirectMessage dm = dmsById.get(dmId);
		if (dm != null)
			dm.markRead();
	}
}

//─────────────────────────────────────────────
//FACADE - ChatApplication (entry point)
//─────────────────────────────────────────────

class ChatApplication {
	private final AuthService authService;
	private final GroupService groupService;
	private final PostService postService;
	private final CommentService commentService;
	private final NotificationService notificationService;
	private final DirectMessageService dmService;

	public ChatApplication() {
		AuthServiceImpl auth = new AuthServiceImpl();
		NotificationServiceImpl notif = new NotificationServiceImpl(auth);
		GroupServiceImpl group = new GroupServiceImpl(auth);
		PostServiceImpl post = new PostServiceImpl(auth, group, notif);
		CommentServiceImpl comment = new CommentServiceImpl(auth, post, group, notif);
		DirectMessageServiceImpl dm = new DirectMessageServiceImpl(auth);

		this.authService = auth;
		this.groupService = group;
		this.postService = post;
		this.commentService = comment;
		this.notificationService = notif;
		this.dmService = dm;
	}

	public AuthService auth() {
		return authService;
	}

	public GroupService groups() {
		return groupService;
	}

	public PostService posts() {
		return postService;
	}

	public CommentService comments() {
		return commentService;
	}

	public NotificationService notifications() {
		return notificationService;
	}

	public DirectMessageService dm() {
		return dmService;
	}
}

//─────────────────────────────────────────────
//DEMO / MAIN
//─────────────────────────────────────────────

public class ChatApp_LLD {
	public static void main(String[] args) {
		ChatApplication app = new ChatApplication();

		// 1. Sign Up
		app.auth().signUp("alice", "alice@example.com", "password123");
		app.auth().signUp("bob", "bob@example.com", "securepass");
		app.auth().signUp("charlie", "charlie@example.com", "charlie99");

		// 2. Sign In
		Session aliceSession = app.auth().signIn("alice@example.com", "password123");
		Session bobSession = app.auth().signIn("bob@example.com", "securepass");
		Session charlieSession = app.auth().signIn("charlie@example.com", "charlie99");

		// 3. Create Group
		Group techGroup = app.groups().createGroup(aliceSession.getSessionToken(), "Tech Talk", "All about tech",
				GroupVisibility.PUBLIC);

		// 4. Bob joins the group
		app.groups().joinGroup(bobSession.getSessionToken(), techGroup.getGroupId());
		app.groups().joinGroup(charlieSession.getSessionToken(), techGroup.getGroupId());

		// 5. Alice makes Charlie a Moderator
		app.groups().changeRole(aliceSession.getSessionToken(), techGroup.getGroupId(),
				app.auth().validateSession(charlieSession.getSessionToken()).getUserId(), Role.MODERATOR);

		// 6. Bob posts to group
		Post post1 = app.posts().createPost(bobSession.getSessionToken(), techGroup.getGroupId(),
				"Hello @alice, excited to be here! Check out this Java LLD.", List.of());

		// 7. Alice comments
		app.comments().addComment(aliceSession.getSessionToken(), post1.getPostId(), "Great post Bob!", null);

		// 8. Charlie (Moderator) hides Bob's post
		app.posts().hidePost(charlieSession.getSessionToken(), post1.getPostId(), "Violates community guidelines.");

		// 9. Alice unhides the post (she's GROUP_ADMIN)
		app.posts().unhidePost(aliceSession.getSessionToken(), post1.getPostId());

		// 10. Alice pins the post
		app.posts().pinPost(aliceSession.getSessionToken(), post1.getPostId());

		// 11. Bob reacts
		app.posts().reactToPost(bobSession.getSessionToken(), post1.getPostId(), ReactionType.LIKE);

		// 12. Alice sends Bob a DM
		app.dm().sendDM(aliceSession.getSessionToken(),
				app.auth().validateSession(bobSession.getSessionToken()).getUserId(), "Hey Bob, welcome to the group!");

		// 13. Get Feed
		List<Post> feed = app.posts().getGroupFeed(aliceSession.getSessionToken(), techGroup.getGroupId(), 0, 10);
		System.out.println("[FEED] Posts visible: " + feed.size());

		// 14. Get Notifications
		List<Notification> notifs = app.notifications().getNotifications(aliceSession.getSessionToken());
		System.out.println("[NOTIF] Alice has " + notifs.size() + " notifications.");

		// 15. Sign out
		app.auth().signOut(aliceSession.getSessionToken());
		System.out.println("[AUTH] Alice signed out.");

		System.out.println("\n✅ Chat Application LLD Demo Complete!");
	}
}
