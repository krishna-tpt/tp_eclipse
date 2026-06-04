package chatapplication_latest.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import chatapplication_latest.model.Comment;

/**
 * In-memory repository for Comments.
 */
public class CommentRepository {

    private final Map<String, Comment> byId = new ConcurrentHashMap<>();

    public void save(Comment comment) {
        byId.put(comment.getId(), comment);
    }

    public Optional<Comment> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    public List<Comment> findByPostId(String postId) {
        return byId.values().stream()
                .filter(c -> c.getPostId().equals(postId))
                .collect(Collectors.toList());
    }

    public List<Comment> findVisibleByPostId(String postId) {
        return byId.values().stream()
                .filter(c -> c.getPostId().equals(postId) && c.isVisible())
                .collect(Collectors.toList());
    }
}
