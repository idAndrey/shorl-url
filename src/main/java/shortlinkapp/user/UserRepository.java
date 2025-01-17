package shortlinkapp.user;

import java.util.*;

public class UserRepository {

    private final Map<UUID, User> storage = new HashMap<>();
    private final Map<String, UUID> storageUser = new HashMap<>();

    public User saveUser(User user) {
        storage.put(user.getUserUuid(), user);
        storageUser.put(user.getUserName(), user.getUserUuid());
        return user;
    }

    public User findUser(UUID uuid) {
        return storage.get(uuid);
    }
    public User getUserByID(String name) {
        return storage.get(storageUser.get(name));
    }

    public void deleteUser(UUID uuid) {
        storage.remove(uuid);
    }

    @SuppressWarnings("unused")
    public Collection<User> findAll() {
        return storage.values();
    }
}
