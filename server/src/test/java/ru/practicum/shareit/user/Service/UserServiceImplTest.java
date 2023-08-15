package ru.practicum.shareit.user.Service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private UserService userService;
    @Mock
    UserRepository userRepository;

    private final EasyRandom easyRandom = new EasyRandom();

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void getUsers() {
        User user1 = easyRandom.nextObject(User.class);
        User user2 = easyRandom.nextObject(User.class);
        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));
        List<User> list = userService.getUsers();
        assertEquals(2, list.size());
    }

    @Test
    void getUserById() throws BadRequestException {
        User user1 = easyRandom.nextObject(User.class);
        when(userRepository.getById(anyLong()))
                .thenReturn(user1);
        UserDto user = userService.getUserById(user1.getId());
        assertEquals(user.getId(), user.getId());
    }

    @Test
    void getUserByIdException() {
        userService.setId(-1L);
        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }


    @Test
    void createUser() throws BadRequestException, CloneNotSupportedException {
        UserDto user1 = easyRandom.nextObject(UserDto.class);
        user1.setEmail("us@us.ru");
        when(userRepository.findAll())
                .thenReturn(new ArrayList<>());
        User user = userService.createUser(user1);
        assertEquals(user1.getName(), user.getName());
    }

    @Test
    void createUserExceptionEmail() throws BadRequestException, CloneNotSupportedException {
        UserDto user1 = easyRandom.nextObject(UserDto.class);
        user1.setEmail("usus.ru");
        assertThrows(BadRequestException.class, () -> userService.createUser(user1));
    }

    @Test
    void createUserExceptionEmail2() throws BadRequestException, CloneNotSupportedException {
        UserDto user1 = easyRandom.nextObject(UserDto.class);
        user1.setEmail(null);
        assertThrows(BadRequestException.class, () -> userService.createUser(user1));
    }

    @Test
    void updateUserById() throws BadRequestException, CloneNotSupportedException {
        UserDto user1 = new UserDto();
        user1.setEmail("user@user.ru");
        User user = easyRandom.nextObject(User.class);
        user.setEmail("userUs@us.us");
        when(userRepository.findAll())
                .thenReturn(new ArrayList<>());
        when(userRepository.getById(anyLong()))
                .thenReturn(user);
        User newUser = userService.updateUserById(user.getId(), user1);
        assertEquals(user1.getEmail(), newUser.getEmail());
    }

    @Test
    void updateUserByIdException() throws BadRequestException, CloneNotSupportedException {
        UserDto user1 = new UserDto();
        user1.setEmail("user@user.ru");
        User user = easyRandom.nextObject(User.class);
        user.setEmail("user@user.ru");
        when(userRepository.findAll())
                .thenReturn(new ArrayList<>(List.of(user)));
        assertThrows(CloneNotSupportedException.class, () -> userService.createUser(user1));
    }

    @Test
    void returnId() {
        Long numb = userService.returnId();
        assertEquals(0, numb);
    }
}