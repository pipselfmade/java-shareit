package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.user.UserMapper.toUser;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplIT {
    private final UserDto user1 = new UserDto(1L, "1", "1@mail.com");
    private final UserDto user2 = new UserDto(2L, "2", "2@mail.com");
    private final UserDto user3 = new UserDto(3L, "3", "3@mail.com");
    private final UserDto user4 = new UserDto(4L, "4", "4@mail.com");

    private final UserService service;

    @MockBean
    private final UserRepository userRepository;

    @Test
    void createNewUser_returnUserDto() {
        when(userRepository.save(any(User.class)))
                .thenReturn(toUser(user1));

        UserDto user = service.addUser(user1);

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(user1.getName()));
        assertThat(user.getEmail(), equalTo(user1.getEmail()));
    }

    @Test
    void getAllUsers_returnListDto_existLength4() {
        List<User> users = List.of(toUser(user1), toUser(user2), toUser(user3), toUser(user4));

        when(userRepository.findAll())
                .thenReturn(users);

        List<User> usersDto = service.getUsers();

        assertNotNull(usersDto, "Юзеров нет");
        assertEquals(4, usersDto.size(), "Количесвто юзеров не совпадает");
    }

    @Test
    void searchUserById_returnUserOne_existUserOne() {
        UserDto user = new UserDto(1L, "name1", "emai1@mail.com");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(toUser(user)));

        UserDto foundUser = service.getUserById(user.getId());

        assertNotNull(foundUser, "Пользователь пуст");
        assertEquals(user.getId(), foundUser.getId(), "Ид не совпадает");
        assertEquals(user.getName(), foundUser.getName(), "имя не совпадает");
        assertEquals(user.getEmail(), foundUser.getEmail(), "емейл не совпадает");
    }

    @Test
    void searchUserById_expectedThrow_user99notExist() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getUserById(99L), "Юзер 99 обнаружен");
    }

    @Test
    void getUserById_correctResult() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(toUser(user1)));

        assertEquals(service.getUserById(1L), user1);
    }

    @Test
    void getUserById_incorrectUser_throwsException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getUserById(99L), "Юзер 99 обнаружен");
    }

    @Test
    void updateUser_correctResult() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(toUser(user1)));

        when(userRepository.save(any(User.class)))
                .thenReturn(toUser(user1));

        UserDto user = service.updateUser(1L, user1);

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(user1.getName()));
        assertThat(user.getEmail(), equalTo(user1.getEmail()));
    }

    @Test
    void deleteUser_correctResult1() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        assertEquals(service.deleteUser(1L), true);
    }

    @Test
    void deleteUser_correctResult2() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        assertEquals(service.deleteUser(1L), false);
    }
}
