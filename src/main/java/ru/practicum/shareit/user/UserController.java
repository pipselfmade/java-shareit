package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.DuplicateException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    public List<User> getUsers() {
        return service.getUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return service.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public Boolean deleteUser(@PathVariable Long userId) {
        return service.deleteUser(userId);
    }

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto user) throws DuplicateException {
        return service.addUser(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId,
                              @RequestBody UserDto user) throws DuplicateException {
        return service.updateUser(userId, user);
    }
}
