package ru.practicum.shareit.user.Service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getUsers();

    UserDto getUserById(Long id);

    User createUser(UserDto user) throws CloneNotSupportedException;

    User updateUserById(Long id, UserDto user) throws CloneNotSupportedException;

    void deleteUserById(Long id);

    Long returnId();

    void setId(Long id);
}
