package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.user.Service.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getUsers() {
        log.info("UserList получен(UserController)");
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        log.info("User получен(UserController): {}", id);
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@RequestBody UserDto user) throws CloneNotSupportedException {
        log.info("User добавлен(UserController): {}", user);
        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    public User updateUserByIdPatch(@PathVariable Long userId,
                                    @RequestBody UserDto user)
            throws CloneNotSupportedException, BadRequestException {
        log.info("User обновлен(UserController): {}, {}", userId, user);
        return userService.updateUserById(userId, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        log.info("User " + id + " удален(UserController)");
        userService.deleteUserById(id);
    }
}
