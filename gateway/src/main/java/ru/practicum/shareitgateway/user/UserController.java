package ru.practicum.shareitgateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@Slf4j
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("New GET /users request");
        return userClient.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        log.info("New GET /users/{} request", id);
        return userClient.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto user) {
        log.info("New POST /users request");
        return userClient.createUser(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUserByIdPatch(@PathVariable Long userId,
                                                      @Valid @RequestBody UserDto user) {
        log.info("New PATCH /users/{} request", userId);
        return userClient.updateUserById(userId, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUserById(@PathVariable Long id) {
        log.info("New DELETE /users/{} request", id);
        return userClient.deleteUserById(id);
    }
}
