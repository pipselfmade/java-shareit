package ru.practicum.shareit.item.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.Service.UserService;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ItemServiceImplIntegrationTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    ItemService itemService;
    @Autowired
    UserService userService;
    @Autowired
    ItemRequestService itemRequestService;
    ItemRequest itemRequest;
    ItemRequest itemRequest2;
    Item item;
    User user;
    User user2;
    Booking booking;
    Comment comment;

    @BeforeEach
    public void setUp() {
        user = new User(1L, "name", "user@user.ru");
        userRepository.save(user);
        user2 = new User(2L, "name2", "user2@user.ru");
        userRepository.save(user2);
        userService.setId(2L);
        itemRequest = new ItemRequest(1L, "description", user, LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
        itemRequest2 = new ItemRequest(2L, "description2", user2, LocalDateTime.now());
        itemRequestRepository.save(itemRequest2);
        itemRequestService.setId(2L);
        item = new Item(1L, user, "name", "Description", true, itemRequest);
        itemRepository.save(item);
    }

    @Test
    @DirtiesContext
    void getItems() {
        List<ItemDto> list = itemService.getItems(1L, 0, 10);
        assertEquals(1, list.size());
    }

    @Test
    @DirtiesContext
    void getItemById() {
        itemService.setId(2L);
        ItemDto itemDto = itemService.getItemById(1L, 1L);
        assertEquals(itemDto.getName(), item.getName());
    }

    @Test
    @DirtiesContext
    void getItemsText() {
        List<ItemDto> list = itemService.getItemsText("description", 0, 10);
        assertEquals(list.get(0).getId(), item.getId());
    }

    @Test
    @DirtiesContext
    void createComment() throws BadRequestException {
        itemService.setId(2L);
        booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.WAITING);
        bookingRepository.save(booking);
        comment = new Comment(1L, "desc", item, user, LocalDateTime.now());
        commentRepository.save(comment);
        CommentDto commentDto = new CommentDto(1L, "Text", "Vasia", LocalDateTime.now());
        CommentDto checkCommentDto = itemService.createComment(commentDto, 1L, 1L);
        assertEquals(checkCommentDto.getText(), commentDto.getText());
    }

    @Test
    @DirtiesContext
    void createItem() throws BadRequestException {
        ItemDto itemDto = new ItemDto(1L, "NEWname", "NewDescription", true, null,
                null, List.of(), 1L);
        ItemDto itemDto1 = itemService.createItem(1L, itemDto);
        assertEquals(itemDto1.getName(), itemDto.getName());
    }

    @Test
    @DirtiesContext
    void updateItemById() throws BadRequestException, CloneNotSupportedException {
        ItemDto itemDto = new ItemDto(1L, "NEWname", null, null, null,
                null, List.of(), 1L);
        ItemDto itemDto1 = itemService.updateItemById(1L, 1L, itemDto);
        assertEquals(itemDto1.getName(), itemDto.getName());
        assertEquals(itemDto1.getDescription(), item.getDescription());
    }

    @Test
    @DirtiesContext
    void deleteItemById() {
        itemService.setId(2L);
        itemService.deleteItemById(1L);
        assertEquals(itemService.getItems(1L, 0, 10).size(), 0);
    }

    @Test
    @DirtiesContext
    void returnId() {
        assertEquals(0, itemService.returnId());
        itemService.setId(2L);
        assertEquals(2L, itemService.returnId());
    }
}