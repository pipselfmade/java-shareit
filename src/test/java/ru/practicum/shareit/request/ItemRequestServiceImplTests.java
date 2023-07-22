package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServiceImpl;

import javax.xml.bind.ValidationException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.request.ItemRequestMapper.toRequestDto;
import static ru.practicum.shareit.user.UserMapper.toUser;
import static ru.practicum.shareit.user.UserMapper.toUserDto;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTests {
    private final ItemRequestServiceImpl itemRequestService;
    private final UserServiceImpl userService;
    private final Timestamp now = Timestamp.valueOf(LocalDateTime.now());

    private User user = new User();
    private User user1 = new User();
    private User user2 = new User();

    private ItemRequest itemRequest = new ItemRequest();
    private ItemRequest itemRequest1 = new ItemRequest();

    private final Item item = new Item();

    @BeforeEach
    public void beforeEach() {
        user = User.builder().id(1L).name("test").email("test@test.ru").build();
        user = toUser(userService.addUser(toUserDto(user)));
        user1 = User.builder().id(2L).name("test1").email("test1@test.ru").build();
        user = toUser(userService.addUser(toUserDto(user1)));
        user2 = User.builder().id(3L).name("test2").email("test2@test.ru").build();
        user = toUser(userService.addUser(toUserDto(user2)));
        itemRequest = ItemRequest.builder().id(1L).requestor(user).created(now).description("test").build();
        itemRequest1 = ItemRequest.builder().id(2L).requestor(user1).created(now).description("test1").build();
    }

    @Test
    void addRequest() {
        ItemRequestDto testItem = itemRequestService.addRequest(user.getId(), toRequestDto(itemRequest));

        assertEquals(1L, testItem.getId());
        assertEquals(3L, testItem.getRequestor().getId());
        assertNotNull(testItem.getCreated());
        assertNotEquals(now, testItem.getCreated());
        assertTrue(now.before(testItem.getCreated()));
        assertEquals("test", testItem.getDescription());
    }

    @Test
    void addRequest_invalidUser() {
        assertThrows(NotFoundException.class, () -> {
            ItemRequestDto itemRequestDto = toRequestDto(itemRequest);
            itemRequestService.addRequest(100L, itemRequestDto);
        });
    }

    @Test
    void getOwnRequests() throws ValidationException {
        ItemRequestDto testItem = itemRequestService.addRequest(1L, toRequestDto(itemRequest));
        ItemRequestDto testItem2 = itemRequestService.addRequest(2L, toRequestDto(itemRequest1));

        List<ItemRequestWithItems> testOwnRequests = itemRequestService.getOwnRequests(1L, 0, 10);
        List<ItemRequestWithItems> testOwnRequests1 = itemRequestService.getOwnRequests(2L, 0, 10);

        assertEquals(1, testOwnRequests.size());
        assertEquals(1, testOwnRequests1.size());
        assertEquals("test", testOwnRequests.get(0).getDescription());
        assertEquals("test1", testOwnRequests1.get(0).getDescription());
    }

    @Test
    void getOwnRequests_invalidUser() {
        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getOwnRequests(100L, 0, 10);
        });
    }

    @Test
    void getAll() throws ValidationException {
        ItemRequestDto testItem = itemRequestService.addRequest(1L, toRequestDto(itemRequest));
        ItemRequestDto testItem2 = itemRequestService.addRequest(2L, toRequestDto(itemRequest1));

        List<ItemRequestWithItems> testOwnRequests = itemRequestService.getAll(1L, 0, 10);
        List<ItemRequestWithItems> testOwnRequests1 = itemRequestService.getAll(2L, 0, 10);
        List<ItemRequestWithItems> testOwnRequests2 = itemRequestService.getAll(3L, 0, 10);

        assertEquals(1, testOwnRequests.size());
        assertEquals(1, testOwnRequests1.size());
        assertEquals(2, testOwnRequests2.size());
        assertEquals("test1", testOwnRequests.get(0).getDescription());
        assertEquals("test", testOwnRequests1.get(0).getDescription());
        assertEquals("test1", testOwnRequests2.get(0).getDescription());
        assertEquals("test", testOwnRequests2.get(1).getDescription());
    }

    @Test
    void getAll_invalidUser() {
        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getAll(100L, 0, 10);
        });
    }

    @Test
    void getRequestById() {
        ItemRequestDto testItem = itemRequestService.addRequest(1L, toRequestDto(itemRequest));
        ItemRequestDto testItem2 = itemRequestService.addRequest(2L, toRequestDto(itemRequest1));

        Long firstId = testItem.getId();
        Long secondId = testItem2.getId();

        assertEquals(1L, firstId);
        assertEquals(2L, secondId);
        assertEquals("test", itemRequestService.getRequestById(1L, firstId).getDescription());
        assertEquals("test1", itemRequestService.getRequestById(2L, secondId).getDescription());
        assertEquals("test", itemRequestService.getRequestById(2L, firstId).getDescription());
        assertEquals("test1", itemRequestService.getRequestById(1L, secondId).getDescription());
        assertEquals("test", itemRequestService.getRequestById(3L, firstId).getDescription());
        assertEquals("test1", itemRequestService.getRequestById(3L, secondId).getDescription());
    }

    @Test
    void getRequestById_invalidUser() {
        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequestById(100L, 1L);
        });
    }

    @Test
    void getRequestById_invalidRequest() {
        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequestById(user.getId(), 100L);
        });
    }
}
