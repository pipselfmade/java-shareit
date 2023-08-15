package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;

    @BeforeEach
    public void saveObject() {
        User user = new User(1L, "name", "user@user.ru");
        userRepository.save(user);
        User user2 = new User(2L, "name2", "user2@user.ru");
        userRepository.save(user2);
        ItemRequest itemRequest = new ItemRequest(1L, "name", user, LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
        ItemRequest itemRequest2 = new ItemRequest(2L, "name", user2, LocalDateTime.now());
        itemRequestRepository.save(itemRequest2);
        Item item1 = new Item(1L, user, "name", "description", true, itemRequest);
        itemRepository.save(item1);
    }

    @AfterEach
    public void clearRepository() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void getAllItemRequestForUser() {
        List<ItemRequest> lists = itemRequestRepository.getAllItemRequestForUser(1L);
        assertEquals(1, lists.get(0).getId());
    }

    @Test
    void getAllItemRequestForUserNull() {
        List<ItemRequest> lists = itemRequestRepository.getAllItemRequestForUserNull(1L, Pageable.ofSize(1));
        assertEquals(1, lists.size());
        assertEquals(2, lists.get(0).getId());
    }
}