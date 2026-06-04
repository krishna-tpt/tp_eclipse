package chatapplication_latest.enums;

public enum PostVisibility {
    PUBLIC,
    GROUP_ONLY,
    SELECTED_MEMBERS,  // Admin-only: share with specific members (like Slack private posts)
    HIDDEN
}
