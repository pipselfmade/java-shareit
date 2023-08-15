package ru.practicum.shareit.user.Service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserServiceImplIntegrationTest {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    private final EasyRandom easyRandom = new EasyRandom();
    User user;
    User user2;

    @BeforeEach
    public void setUp() {
        user = new User(1L, "name", "user@user.ru");
        userRepository.save(user);
        user2 = new User(2L, "name2", "user2@user.ru");
    }

    @Test
    @DirtiesContext
    void getUsers() {
        List<User> checkUser = userService.getUsers();
        assertEquals(1, checkUser.size());
        assertEquals(checkUser.get(0), user);
    }

    @Test
    @DirtiesContext
    void getUserById() throws BadRequestException {
        userService.setId(2L);
        UserDto checkUser = userService.getUserById(1L);
        assertEquals(checkUser.getName(), user.getName());
    }

    @Test
    @DirtiesContext
    void createUser() throws BadRequestException, CloneNotSupportedException {
        UserDto userDto = easyRandom.nextObject(UserDto.class);
        userDto.setEmail("userUs@user.ru");
        User user = userService.createUser(userDto);
        assertEquals(user.getName(), userDto.getName());
    }

    @Test
    @DirtiesContext
    void updateUserById() throws BadRequestException, CloneNotSupportedException {
        UserDto userDto = new UserDto(1L, "Update", null);
        User checkUser = userService.updateUserById(1L, userDto);
        assertEquals(checkUser.getName(), "Update");
    }

    @Test
    @DirtiesContext
    void deleteUserById() {
        userService.deleteUserById(1L);
        assertEquals(0, userService.getUsers().size());
    }

    @Test
    @DirtiesContext
    void returnId() {
        assertEquals(0, userService.returnId());
        userService.setId(2L);
        assertEquals(2, userService.returnId());
    }
}