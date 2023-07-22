package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTests {
    private final UserServiceImpl userService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private UserDto createUserDto(String name, String email) {
        return UserDto.builder().name(name).email(email).build();
    }

    @BeforeEach
    public void beforeEach() {
        userService.addUser(createUserDto("test", "test@test.ru"));
    }

    @Test
    void getUsers() {
        List<User> users = userService.getUsers();

        assertEquals(1, users.size());
        assertEquals("test", users.get(0).getName());
        assertEquals("test@test.ru", users.get(0).getEmail());
    }

    @Test
    void getUserById() {
        UserDto userDto = userService.addUser(createUserDto("test2", "test2@test.ru"));
        Long userId = userDto.getId();
        UserDto retrievedUser = userService.getUserById(userId);

        assertNotNull(retrievedUser);
        assertEquals("test2", retrievedUser.getName());
        assertEquals("test2@test.ru", retrievedUser.getEmail());
    }

    @Test
    void getUserById_invalidUser() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(100L));
    }

    @Test
    void addUser() {
        UserDto userDto = createUserDto("test3", "test3@test.ru");
        UserDto addedUser = userService.addUser(userDto);

        assertNotNull(addedUser);
        assertNotNull(addedUser.getId());
        assertEquals("test3", addedUser.getName());
        assertEquals("test3@test.ru", addedUser.getEmail());

        Optional<User> retrievedUser = userRepository.findById(addedUser.getId());

        assertTrue(retrievedUser.isPresent());
        assertEquals("test3", retrievedUser.get().getName());
        assertEquals("test3@test.ru", retrievedUser.get().getEmail());
    }

    @Test
    void updateUser_invalidUser() {
        UserDto userDto = UserDto.builder().name("test").email("test@test.ru").build();

        assertThrows(NotFoundException.class, () -> userService.updateUser(100L, userDto));
    }

    @Test
    void updateUser() {
        UserDto userDto = userService.addUser(createUserDto("test4", "test4@test.ru"));
        Long userId = userDto.getId();
        UserDto updatedUser = userService.updateUser(userId, createUserDto("updated", "updated@test.ru"));

        assertNotNull(updatedUser);
        assertEquals(userId, updatedUser.getId());
        assertEquals("updated", updatedUser.getName());
        assertEquals("updated@test.ru", updatedUser.getEmail());

        Optional<User> retrievedUser = userRepository.findById(userId);

        assertTrue(retrievedUser.isPresent());
        assertEquals("updated", retrievedUser.get().getName());
        assertEquals("updated@test.ru", retrievedUser.get().getEmail());
    }

    @Test
    void deleteUser() {
        UserDto userDto = userService.addUser(createUserDto("test5", "test5@test.ru"));
        Long userId = userDto.getId();
        boolean isDeleted = userService.deleteUser(userId);

        assertTrue(isDeleted);

        Optional<User> deletedUser = userRepository.findById(userId);

        assertFalse(deletedUser.isPresent());

        List<Item> items = new ArrayList<>(itemRepository.findAllByOwnerId(userId));

        assertTrue(items.isEmpty());
    }
}
