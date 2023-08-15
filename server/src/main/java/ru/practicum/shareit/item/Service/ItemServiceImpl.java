package ru.practicum.shareit.item.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingItemEntity;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.Service.UserService;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.booking.mapper.BookingMapper.makeBookingItemEntity;
import static ru.practicum.shareit.item.comment.CommentMapper.makeCommentDto;
import static ru.practicum.shareit.item.comment.CommentMapper.makeCommentDtoList;
import static ru.practicum.shareit.item.mapper.ItemMapper.makeItem;
import static ru.practicum.shareit.item.mapper.ItemMapper.makeItemDto;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    private Long id = 0L;
    private Long commentId = 0L;

    @Transactional
    @Override
    public List<ItemDto> getItems(Long userId, Integer from, Integer size) {
        List<ItemDto> itemList = new ArrayList<>();
        ItemDto itemDto;
        Booking next;
        Booking last;

        int page = Math.round((float) from / size);
        Pageable pageable = PageRequest.of(page, size).withSort(Sort.by("id").descending());

        for (Item it : itemRepository.findAllItemWhereOwner(userId, pageable)) {
            itemDto = makeItemDto(it);
            next = bookingRepository.getNextBookingForItem(it.getId(), LocalDateTime.now()).orElse(null);
            last = bookingRepository.getLastBookingForItem(it.getId(), LocalDateTime.now()).orElse(null);
            if (next != null) {
                itemDto.setNextBooking(makeBookingItemEntity(next));
            } else {
                itemDto.setNextBooking(null);
            }
            if (last != null) {
                itemDto.setLastBooking(makeBookingItemEntity(last));
            } else {
                itemDto.setLastBooking(null);
            }

            itemDto.setComments(makeCommentDtoList(commentRepository.getCommentsForItem(itemDto.getId())));
            itemList.add(itemDto);
        }
        return itemList;
    }

    @Transactional
    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        Item item;

        if (itemId <= returnId()) {
            item = itemRepository.getById(itemId);
        } else {
            throw new NotFoundException("Заданного Item id не существует");
        }

        Booking next = null;
        Booking last = null;

        if (item.getOwner().getId().equals(userId)) {
            next = bookingRepository.getNextBookingForItem(itemId, LocalDateTime.now()).orElse(null);
            last = bookingRepository.getLastBookingForItem(itemId, LocalDateTime.now()).orElse(null);
        }

        BookingItemEntity nextDto = null;
        BookingItemEntity lastDto = null;

        if (next != null) {
            nextDto = makeBookingItemEntity(next);
        }
        if (last != null) {
            lastDto = makeBookingItemEntity(last);
        }

        ItemDto itemDto = makeItemDto(item);
        itemDto.setNextBooking(nextDto);
        itemDto.setLastBooking(lastDto);

        List<CommentDto> comment = makeCommentDtoList(commentRepository.getCommentsForItem(itemId));
        itemDto.setComments(comment);
        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsText(String text, Integer from, Integer size) {
        List<ItemDto> itemList = new ArrayList<>();
        String checkingTheComparisonName;
        String checkingTheComparisonDescription;

        int page = Math.round((float) from / size);
        Pageable pageable = PageRequest.of(page, size).withSort(Sort.by("id").descending());

        if (text == null || text.equals("")) {
            return new ArrayList<>();
        } else {
            for (Item it : itemRepository.findAllItem(pageable)) {
                Item itemM = it;
                if (itemM.getAvailable()) {
                    checkingTheComparisonName = itemM.getName().toLowerCase();
                    checkingTheComparisonDescription = itemM.getDescription().toLowerCase();
                    if (checkingTheComparisonName.contains(text.toLowerCase()))
                        itemList.add(makeItemDto(itemM));
                    else if (checkingTheComparisonDescription.contains(text.toLowerCase()))
                        itemList.add(makeItemDto(itemM));
                }
            }
            return itemList;
        }
    }

    @Transactional
    public CommentDto createComment(CommentDto commentDto, Long userId, Long itemId) {
        if (userId > userService.returnId()) {
            throw new NotFoundException("Данного юзера не существует (Item.createComment)");
        }
        if (itemId > returnId()) {
            throw new BadRequestException("Данная вещь отсутствует (Item.createComment)");
        }
        if (commentDto.getText() == null || commentDto.getText().equals("")) {
            throw new BadRequestException("Комментарий пустой (Item.createComment)");
        }

        BookingStatus bookingStatus = bookingRepository.checkStatusOfBooking(userId, itemId, LocalDateTime.now());
        if (bookingStatus == null) {
            throw new BadRequestException("Бронирование отсутствует (Item.createComment)");
        }

        commentDto.setId(makeCommentId());
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        comment.setItem(itemRepository.getById(itemId));
        comment.setAuthor(userRepository.getById(userId));
        comment.setCreated(LocalDateTime.now());
        commentDto.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        CommentDto commentDto1 = makeCommentDto(comment);
        return commentDto1;
    }

    @Transactional
    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User owner = userRepository.getById(userId);

        if (itemDto.getAvailable() == null) {
            throw new BadRequestException("Поле Available отсутствует");
        }
        if (itemDto.getName() == null || itemDto.getName().equals("")) {
            throw new BadRequestException("Поле Name отсутствует");
        }
        if (itemDto.getDescription() == null) {
            throw new BadRequestException("Поле Description отсутствует");
        }
        if (userService.getUserById(userId) == null) {
            throw new NotFoundException("Поле User отсутствует");
        }

        itemDto.setId(makeId());
        Item item = makeItem(itemDto);

        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new NotFoundException("Добавленный запрос отсутствует (ItemService.create)"));
            item.setRequestId(itemRequest);
        }

        item.setId(itemDto.getId());
        item.setOwner(owner);
        itemRepository.save(item);
        return itemDto;
    }

    @Transactional
    @Override
    public ItemDto updateItemById(Long userId, Long id, ItemDto itemDto) {
        Item adItem = itemRepository.getById(id);
        User verificationUser = adItem.getOwner();

        if (!verificationUser.getId().equals(userId)) {
            throw new NotFoundException("Поле Owner не совпадает");
        }
        if (itemDto.getId() != null) {
            adItem.setId(id);
        }
        if (itemDto.getName() != null) {
            adItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            adItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            adItem.setAvailable(itemDto.getAvailable());
        }

        itemRepository.save(adItem);
        itemDto = makeItemDto(adItem);
        return itemDto;
    }

    @Override
    public void deleteItemById(Long id) {
        itemRepository.delete(itemRepository.getById(id));
    }

    @Override
    public Long returnId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    private Long makeId() {
        id += 1;
        return id;
    }

    private Long makeCommentId() {
        commentId += 1;
        return commentId;
    }
}
