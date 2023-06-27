package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User createUser(User user);

    Optional<User> getUser(Long id);

    List<User> getAllUsers();

    User updateUser(User user, Long id);

    Optional<User> deleteUser(Long id);

    Boolean checkDuplicateEmail(String email);
}
