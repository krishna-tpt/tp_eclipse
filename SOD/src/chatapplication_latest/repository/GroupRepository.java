package chatapplication_latest.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import chatapplication_latest.model.Group;

/**
 * In-memory repository for Groups.
 */
public class GroupRepository {

    private final Map<String, Group> byId = new ConcurrentHashMap<>();

    public void save(Group group) {
        byId.put(group.getId(), group);
    }

    public Optional<Group> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    public List<Group> findAll() {
        return new ArrayList<>(byId.values());
    }

    public List<Group> findByMemberId(String userId) {
        return byId.values().stream()
                .filter(g -> g.isMember(userId))
                .collect(Collectors.toList());
    }
}
