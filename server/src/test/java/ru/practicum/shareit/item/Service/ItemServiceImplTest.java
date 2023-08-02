package ru.practicum.shareit.item.Service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.Service.UserService;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.item.comment.CommentMapper.makeCommentDto;
import static ru.practicum.shareit.user.mapper.UserMapper.makeUserDto;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    private ItemService itemService;
    private final EasyRandom easyRandom = new EasyRandom();
    @Mock
    UserService userService;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    private Item item;
    private Item secondItem;
    private List<Item> itemList;
    private Booking next;
    private Booking last;
    private Comment comment;
    private List<Comment> commentList;
    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    public void setUp() {
        itemService = new ItemServiceImpl(userService, userRepository, itemRepository,
                bookingRepository, commentRepository, itemRequestRepository);
        item = easyRandom.nextObject(Item.class);
        itemList = new ArrayList<>();
        itemList.add(item);
        secondItem = easyRandom.nextObject(Item.class);
        next = easyRandom.nextObject(Booking.class);
        last = easyRandom.nextObject(Booking.class);
        comment = easyRandom.nextObject(Comment.class);
        commentList = new ArrayList<>();
        commentList.add(comment);
        user = easyRandom.nextObject(User.class);
        itemRequest = easyRandom.nextObject(ItemRequest.class);
    }

    @Test
    void getItems() throws BadRequestException {
        when(itemRepository.findAllItemWhereOwner(anyLong(), any()))
                .thenReturn(itemList);
        when(bookingRepository.getNextBookingForItem(anyLong(), any()))
                .thenReturn(Optional.ofNullable(next));
        when(bookingRepository.getLastBookingForItem(anyLong(), any()))
                .thenReturn(Optional.ofNullable(last));
        when(commentRepository.getCommentsForItem(anyLong()))
                .thenReturn(commentList);
        List<ItemDto> listDto = ItemMapper.listToItemDto(itemList);
        List<ItemDto> checkList = itemService.getItems(1L, 0, 10);
        assertEquals(listDto.size(), checkList.size());
    }

    @Test
    void getItemById() {
        Long id = item.getId();
        when(itemRepository.getById(anyLong()))
                .thenReturn(item);
        when(commentRepository.getCommentsForItem(anyLong()))
                .thenReturn(commentList);
        ItemDto checkItemDto = itemService.getItemById(1L, id);
        ItemDto itemDto = ItemMapper.makeItemDto(item);
        assertEquals(checkItemDto.getId(), itemDto.getId());
    }

    @Test
    void getItemsText() {
        when(itemRepository.findAllItem(any())).thenReturn(itemList);
        String text = item.getDescription();
        List<ItemDto> checkItemsDto = itemService.getItemsText(text, 0, 10);
        assertEquals(checkItemsDto.size(), itemList.size());
        ItemDto checkItemDto = checkItemsDto.get(0);
        assertEquals(checkItemDto.getName(), item.getName());
    }

    @Test
    void createComment() throws BadRequestException {
        when(userService.returnId()).thenReturn(item.getOwner().getId());
        Long userId = 1L;
        when(userService.returnId())
                .thenReturn(userId);
        when(bookingRepository.checkStatusOfBooking(anyLong(), any(), any()))
                .thenReturn(BookingStatus.APPROVED);
        when(itemRepository.getById(anyLong())).thenReturn(item);
        when(userRepository.getById(anyLong())).thenReturn(user);
        CommentDto commentDto = makeCommentDto(comment);
        CommentDto checkCommentDto = itemService.createComment(commentDto, userId, item.getId());
        assertEquals(checkCommentDto.getText(), comment.getText());
    }

    @Test
    void createItem() throws BadRequestException {
        when(userRepository.getById(anyLong())).thenReturn(user);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(itemRequest));
        UserDto userDto = makeUserDto(user);
        when(userService.getUserById(anyLong())).thenReturn(userDto);
        ItemDto itemDto = ItemMapper.makeItemDto(item);
        ItemDto checkItemDto = itemService.createItem(1L, itemDto);
        assertEquals(checkItemDto.getName(), item.getName());
    }

    @Test
    void updateItemById() throws BadRequestException, CloneNotSupportedException {
        when(itemRepository.getById(anyLong())).thenReturn(item);
        ItemDto itemDto = ItemMapper.makeItemDto(secondItem);
        ItemDto checkItem = itemService.updateItemById(item.getOwner().getId(), 1L, itemDto);
        assertEquals(checkItem.getName(), item.getName());
    }
}