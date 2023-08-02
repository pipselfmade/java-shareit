package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;

    @BeforeEach
    public void setUp() {
        User user = new User(1L, "name", "user@user.ru");
        userRepository.save(user);
        User user2 = new User(2L, "name2", "user2@user.ru");
        userRepository.save(user2);
        ItemRequest itemRequest = new ItemRequest(1L, "name", user, LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
        Item item1 = new Item(1L, user, "name", "description", true, itemRequest);
        Item item2 = new Item(2L, user2, "name2", "description2", true, itemRequest);
        itemRepository.save(item1);
        itemRepository.save(item2);
    }

    @AfterEach
    public void clearRepository() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void findAllItem() {
        List<Item> items = itemRepository.findAllItem(Pageable.ofSize(2));
        assertEquals(2, items.size());
        assertEquals(1, items.get(0).getId());
    }

    @Test
    void findAllItemWhereOwner() {
        List<Item> items = itemRepository.findAllItemWhereOwner(1L, Pageable.ofSize(1));
        assertEquals(1, items.get(0).getId());
    }

    @Test
    void findAllItemWhereRequester() {
        List<Item> items = itemRepository.findAllItemWhereRequester(1L);
        assertEquals(2, items.size());
    }
}