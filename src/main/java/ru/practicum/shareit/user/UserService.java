package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<User> getUsers();

    UserDto getUserById(Long userId);

    UserDto addUser(UserDto user);

    UserDto updateUser(Long userId, UserDto user);

    Boolean deleteUser(Long userId);
}
