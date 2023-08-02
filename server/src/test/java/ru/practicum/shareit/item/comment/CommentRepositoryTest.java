package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    CommentRepository commentRepository;

    @BeforeEach
    public void setUp() {
        User user = new User(1L, "name", "user@user.ru");
        userRepository.save(user);
        ItemRequest itemRequest = new ItemRequest(1L, "name", user, LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
        Item item1 = new Item(1L, user, "name", "description", true, itemRequest);
        itemRepository.save(item1);
        Comment comment = new Comment(1L, "Text", item1, user, LocalDateTime.now());
        commentRepository.save(comment);
    }

    @AfterEach
    public void clearRepository() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    void getCommentsForItem() {
        List<Comment> list = commentRepository.getCommentsForItem(1L);
        assertEquals(1, list.size());
    }
}