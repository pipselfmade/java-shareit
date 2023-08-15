package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.Service.UserService;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
class ItemRequestServiceImplIntegrationTest {
    @Autowired
    ItemRequestService itemRequestService;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserService userService;
    ItemRequest itemRequest;
    ItemRequest itemRequest2;
    Item item;
    User user;
    User user2;

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
    void getRequests() throws BadRequestException {
        List<ItemRequestDto> list = itemRequestService.getRequests(user.getId());
        assertEquals(1, list.size());
        assertEquals(itemRequest.getId(), list.get(0).getId());
    }

    @Test
    void getRequestsFrom() throws BadRequestException {
        List<ItemRequestDto> list = itemRequestService.getRequestsFrom(1L, 0, 10);
        assertEquals(1, list.size());
        assertEquals(itemRequest2.getId(), list.get(0).getId());
    }

    @Test
    void getRequestsById() throws BadRequestException {
        ItemRequestDto itemRequestDto = itemRequestService.getRequestsById(1L, 1L);
        assertEquals(itemRequestDto.getId(), itemRequest.getId());
    }

    @Test
    @DirtiesContext
    void createRequests() throws BadRequestException {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description3", null, LocalDateTime.now(), List.of());
        ItemRequestDto checkItemRequestDto = itemRequestService.createRequests(2L, itemRequestDto);
        assertEquals(checkItemRequestDto.getDescription(), itemRequestDto.getDescription());
    }
}