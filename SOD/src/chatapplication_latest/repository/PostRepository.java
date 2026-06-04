package chatapplication_latest.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import chatapplication_latest.model.Post;

/**
 * In-memory repository for Posts.
 */
public class PostRepository {

    private final Map<String, Post> byId = new ConcurrentHashMap<>();

    public void save(Post post) {
        byId.put(post.getId(), post);
    }

    public Optional<Post> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    public List<Post> findByAuthorId(String authorId) {
        return byId.values().stream()
                .filter(p -> p.getAuthorId().equals(authorId) && !p.isDeleted())
                .collect(Collectors.toList());
    }

    public List<Post> findByGroupId(String groupId) {
        return byId.values().stream()
                .filter(p -> groupId.equals(p.getGroupId()) && !p.isDeleted())
                .collect(Collectors.toList());
    }

    public List<Post> findAll() {
        return byId.values().stream()
                .filter(p -> !p.isDeleted())
                .collect(Collectors.toList());
    }

    public void delete(String postId) {
        byId.computeIfPresent(postId, (k, p) -> { p.markDeleted(); return p; });
    }
}
