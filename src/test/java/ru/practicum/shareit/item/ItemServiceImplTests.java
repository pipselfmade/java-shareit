package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingEntryDto;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.xml.bind.ValidationException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.BookingMapper.toBooking;
import static ru.practicum.shareit.item.ItemMapper.toItem;
import static ru.practicum.shareit.item.ItemMapper.toItemDto;
import static ru.practicum.shareit.item.comment.CommentMapper.toCommentDto;
import static ru.practicum.shareit.request.ItemRequestMapper.toRequest;
import static ru.practicum.shareit.request.ItemRequestMapper.toRequestDto;
import static ru.practicum.shareit.user.UserMapper.toUser;
import static ru.practicum.shareit.user.UserMapper.toUserDto;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTests {
    private final ItemService itemService;
    private final ItemRequestService itemRequestService;
    private final BookingService bookingService;
    private final UserService userService;

    private User user = new User();
    private User user1 = new User();
    private User user2 = new User();

    private Item item = new Item();
    private Item item1 = new Item();
    private Item item2 = new Item();

    private Comment comment = new Comment();
    private Comment comment1 = new Comment();

    private Booking booking = new Booking();
    private Booking booking1 = new Booking();

    ItemRequest itemRequest = new ItemRequest();
    private final Timestamp now = Timestamp.valueOf(LocalDateTime.now());

    @BeforeEach
    public void beforeEach() throws ValidationException {
        user = User.builder().id(1L).name("test").email("test@test.ru").build();
        user = toUser(userService.addUser(toUserDto(user)));

        item = Item.builder().id(1L).owner(user).description("Test").name("Test").request(itemRequest).available(true).build();
        item = toItem(itemService.addItem(1L, toItemDto(item)));

        user1 = User.builder().id(2L).name("test1").email("test1@test.ru").build();
        user1 = toUser(userService.addUser(toUserDto(user1)));

        item1 = Item.builder().id(2L).owner(user1).description("Test1").name("Test1").request(itemRequest).available(true).build();
        item1 = toItem(itemService.addItem(2L, toItemDto(item1)));

        user2 = User.builder().id(3L).name("test2").email("test2@test.ru").build();
        user2 = toUser(userService.addUser(toUserDto(user2)));

        item2 = Item.builder().id(3L).owner(user2).description("Test2").name("Test2").available(true).build();
        item2 = toItem(itemService.addItem(3L, toItemDto(item2)));

        itemRequest = ItemRequest.builder().id(1L).requestor(user).created(now).description("test").build();
        itemRequest = toRequest(itemRequestService.addRequest(user2.getId(), toRequestDto(itemRequest)));

        comment = Comment.builder().item(item2).author(user).created(LocalDateTime.now()).text("TestComm").build();

        BookingEntryDto bookingEntryDto = BookingEntryDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1)).build();

        BookingDto bookingDto = bookingService.addBooking(2L, bookingEntryDto);
        booking = toBooking(bookingDto);

        BookingEntryDto bookingEntryDto1 = BookingEntryDto.builder()
                .itemId(item1.getId())
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now()).build();

        BookingDto bookingDto1 = bookingService.addBooking(1L, bookingEntryDto1);

        booking1 = toBooking(bookingDto1);
        booking1 = toBooking(bookingService.approveBooking(2L, 2L, true));

        comment1 = Comment.builder().item(item2).author(user).created(LocalDateTime.now()).text("TestComm2").build();
        itemService.addComment(1L, 2L, toCommentDto(comment1));
    }

    @Test
    void getItems() {
        List<ItemDto> itemsTest = itemService.getItems(1L);

        assertNotNull(itemsTest);
        assertEquals(1, itemsTest.size());
        assertEquals("Test", itemsTest.get(0).getDescription());
        assertEquals("Test", itemsTest.get(0).getName());
        assertEquals(1L, itemsTest.get(0).getOwner().getId());

        List<ItemDto> items2Test = itemService.getItems(2L);

        assertNotNull(items2Test);
        assertEquals(1, items2Test.size());
        assertEquals("Test1", items2Test.get(0).getDescription());
        assertEquals("Test1", items2Test.get(0).getName());
        assertEquals(2L, items2Test.get(0).getOwner().getId());

        List<ItemDto> items3Test = itemService.getItems(3L);

        assertNotNull(items3Test);
        assertEquals(1, items3Test.size());
        assertEquals("Test2", items3Test.get(0).getDescription());
        assertEquals("Test2", items3Test.get(0).getName());
        assertEquals(3L, items3Test.get(0).getOwner().getId());
    }

    @Test
    void getItemById() {
        ItemDto itemDto = itemService.getItemById(1L, 1L);

        assertNotNull(itemDto);
        assertEquals(1L, itemDto.getId());
        assertEquals("Test", itemDto.getName());
        assertEquals("Test", itemDto.getDescription());
        assertEquals(1L, itemDto.getOwner().getId());

        ItemDto itemDto1 = itemService.getItemById(2L, 1L);

        assertNotNull(itemDto);
        assertEquals(1L, itemDto.getId());
        assertEquals("Test", itemDto.getName());
        assertEquals("Test", itemDto.getDescription());
        assertEquals(1L, itemDto.getOwner().getId());
        assertNull(itemDto1.getLastBooking());
    }

    @Test
    void getItemById_invalidUser() {
        assertThrows(NotFoundException.class, () -> itemService.getItemById(99L, 1L));
    }

    @Test
    void getItemById_invalidItem() {
        assertThrows(NotFoundException.class, () -> itemService.getItemById(2L, 99L));
    }

    @Test
    void updateItem() {
        ItemDto itemDto = ItemDto.builder()
                .id(999L)
                .name("Updated")
                .description("Updated")
                .owner(user2)
                .available(false).build();
        itemService.updateItem(1L, itemDto, 1L);
        ItemDto testItem = itemService.getItemById(1L, 1L);

        assertEquals(1L, testItem.getId());
        assertEquals("Updated", testItem.getName());
        assertEquals("Updated", testItem.getDescription());
        assertEquals(1L, testItem.getOwner().getId());
        assertThrows(NotFoundException.class, () -> itemService.updateItem(2L, itemDto, 1L));
    }

    @Test
    void updateItem_invalidOwner() {
        ItemDto itemDto = ItemDto.builder()
                .id(999L)
                .name("Updated")
                .description("Updated")
                .owner(user2)
                .available(false).build();

        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, itemDto, 2L));
    }

    @Test
    void updateItem_invalidUser() {
        ItemDto itemDto = ItemDto.builder()
                .id(999L)
                .name("Updated")
                .description("Updated")
                .owner(user2)
                .available(false).build();

        assertThrows(NotFoundException.class, () -> itemService.updateItem(999L, itemDto, 1L));
    }

    @Test
    void searchItem() throws ValidationException {
        ItemDto item11 = itemService.addItem(1L, ItemDto.builder()
                .name("Дрель")
                .description("Дрель эллектрическая")
                .owner(user)
                .available(true).build());
        ItemDto item22 = itemService.addItem(1L, ItemDto.builder()
                .name("Дрель ручная")
                .description("Дрель ручная")
                .owner(user)
                .available(true).build());
        ItemDto item33 = itemService.addItem(1L, ItemDto.builder()
                .name("Отвертка")
                .description("Отвертка эллектрическая")
                .owner(user)
                .available(true).build());
        ItemDto item44 = itemService.addItem(1L, ItemDto.builder()
                .name("Отвертка")
                .description("Отвертка ручная")
                .owner(user)
                .available(false).build());
        Collection<ItemDto> items = itemService.searchItems("Дрель", 0, 10);

        assertThat(items).hasSize(2);
        assertThat(items).contains(item11, item22);

        Collection<ItemDto> items1 = itemService.searchItems("Эллектрическая", 0, 10);

        assertThat(items1).hasSize(2);
        assertThat(items1).contains(item11, item33);

        Collection<ItemDto> items2 = itemService.searchItems("ручная", 0, 10);

        assertThat(items2).hasSize(1);
        assertThat(items2).contains(item22);
    }

    @Test
    void searchItemBlank() throws ValidationException {
        ItemDto item11 = itemService.addItem(1L, ItemDto.builder()
                .name("Дрель").description("Дрель эллектрическая")
                .owner(user)
                .available(true).build());
        ItemDto item22 = itemService.addItem(1L, ItemDto.builder()
                .name("Дрель ручная").description("Дрель ручная")
                .owner(user)
                .available(true).build());
        ItemDto item33 = itemService.addItem(1L, ItemDto.builder()
                .name("Отвертка").description("Отвертка эллектрическая")
                .owner(user)
                .available(true).build());
        Collection<ItemDto> items = itemService.searchItems(" ", 0, 10);

        assertThat(items).hasSize(0);
    }

    @Test
    void searchItem_NoSuchItem() throws ValidationException {
        ItemDto item11 = itemService.addItem(1L, ItemDto.builder()
                .name("Дрель")
                .description("Дрель эллектрическая")
                .owner(user)
                .available(true).build());
        ItemDto item22 = itemService.addItem(1L, ItemDto.builder()
                .name("Дрель ручная")
                .description("Дрель ручная")
                .owner(user)
                .available(true).build());
        ItemDto item33 = itemService.addItem(1L, ItemDto.builder()
                .name("Отвертка")
                .description("Отвертка эллектрическая")
                .owner(user)
                .available(true).build());
        Collection<ItemDto> items = itemService.searchItems("Cтул", 0, 10);

        assertThat(items).hasSize(0);
    }

    @Test
    void addItem() {
        ItemDto testItem = ItemDto.builder()
                .owner(user)
                .name("Updated")
                .description("Updated")
                .available(true).build();

        assertThrows(NotFoundException.class, () -> itemService.addItem(99L, testItem));

        itemService.addItem(1L, testItem);
        List<ItemDto> afterItems = itemService.getItems(1L);

        assertEquals(2, afterItems.size());
        assertEquals("Updated", afterItems.get(1).getName());
        assertEquals("Updated", afterItems.get(1).getDescription());
        assertEquals(1L, afterItems.get(1).getOwner().getId());

        List<ItemDto> items2Dto = itemService.getItems(2L);

        assertEquals(1, items2Dto.size());
    }

    @Test
    void addItem_invalidUser() {
        ItemDto testItem = ItemDto.builder()
                .owner(user)
                .name("Updated")
                .description("Updated")
                .available(true).build();

        assertThrows(NotFoundException.class, () -> itemService.addItem(99L, testItem));
    }

    @Test
    void addItem_invalidRequest() {
        ItemDto testItem = ItemDto.builder()
                .owner(user)
                .name("Updated")
                .description("Updated")
                .available(true)
                .requestId(99L).build();

        assertThrows(NotFoundException.class, () -> itemService.addItem(1L, testItem));
    }

    @Test
    void addComment() {
        ItemDto itemDto = itemService.getItems(2L).get(0);
        assertEquals(1, itemDto.getComments().size());
    }

    @Test
    void addComment_invalidUser() {
        assertThrows(NotFoundException.class, () -> itemService.addComment(99L, 2L, toCommentDto(comment1)));
    }

    @Test
    void addComment_invalidItem() {
        assertThrows(NotFoundException.class, () -> itemService.addComment(1L, 99L, toCommentDto(comment1)));
    }
}
