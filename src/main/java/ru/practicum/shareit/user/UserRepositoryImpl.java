package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    @Override
    public User createUser(User user) {
        if (user.getId() == null) {
            user.setId(++id);
        }

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getUser(Long id) {
        if (!users.containsKey(id)) {
            return Optional.empty();
        }

        return Optional.of(users.get(id));
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public User updateUser(User user, Long id) {
        users.put(id, user);
        return users.get(id);
    }

    @Override
    public Optional<User> deleteUser(Long id) {
        Optional<User> memoryUser = getUser(id);

        if (memoryUser.isEmpty()) {
            return Optional.empty();
        }

        users.remove(id);
        return memoryUser;
    }

    public final Boolean checkDuplicateEmail(String email) {
        for (User listUser : users.values()) {
            if (listUser.getEmail().equals(email)) {
                return true;
            }
        }

        return false;
    }
}