package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.item.ItemMapper.toItem;
import static ru.practicum.shareit.user.UserMapper.toUser;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplIT {
    private final ItemService service;

    @MockBean
    private final ItemRepository itemRepository;

    @MockBean
    private final UserRepository userRepository;

    private final UserDto user1 = new UserDto(
            1L,
            "user1",
            "user1@mail.com"
    );
    private final UserDto user2 = new UserDto(
            2L,
            "user2",
            "user2@mail.com"
    );
    private final CommentDto commentDto1 = new CommentDto(
            1L,
            "text from comment1",
            user2.getName(),
            1L,
            LocalDateTime.now()
    );
    private final ItemDto itemDto2 = new ItemDto(
            2L,
            "item2",
            "descr2",
            true,
            toUser(user1),
            null,
            null,
            null,
            List.of(commentDto1)
    );
    private final ItemDto itemDto1 = new ItemDto(
            1L,
            "item1",
            "descr1",
            true,
            toUser(user1),
            null,
            null,
            null,
            new ArrayList<>()
    );

    @Test
    void getAllItemsByOwnerId_returnListItemDto_length2() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(itemRepository.findAllByOwnerId(anyLong()))
                .thenReturn(List.of(toItem(itemDto1), toItem(itemDto2)));

        List<ItemDto> items = service.getItems(user1.getId());

        assertNotNull(items, "Предметов нет");
        assertEquals(2, items.size(), "Количесвто предметов не совпадает");
    }

    @Test
    void getAllItemsByOwnerId_userNotExist_throwsException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.getItems(user1.getId()));
    }

    @Test
    void getItemById_returnItemDto() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(toItem(itemDto1)));

        ItemDto findItem = service.getItemById(1L, 1L);

        assertEquals(itemDto1.getName(), findItem.getName());
    }

    @Test
    void getItemByID_itemNotFound_throwsException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.getItemById(99L, 1L));
    }

    @Test
    void createNewItem_returnItemDto() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(toUser(user1)));

        when(itemRepository.save(any(Item.class)))
                .thenReturn(toItem(itemDto1));

        ItemDto item = service.addItem(user1.getId(), itemDto1);

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto1.getName()));
        assertThat(item.getDescription(), equalTo(itemDto1.getDescription()));
    }

    @Test
    void createNewItem_incorrectOwner_throwsException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.addItem(user1.getId(), itemDto1));
    }

    @Test
    void updateItem_correctUpdate() {
        ItemDto updateItem = itemDto1;
        updateItem.setAvailable(false);
        updateItem.setDescription("Update");
        updateItem.setName("Update");

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(toItem(updateItem)));

        when(itemRepository.save(any(Item.class)))
                .thenReturn(toItem(updateItem));

        ItemDto itemBeforeUpdate = service.updateItem(itemDto1.getId(), updateItem, user1.getId());

        assertEquals(updateItem.getName(), itemBeforeUpdate.getName(), "Name not update");
        assertEquals(updateItem.getAvailable(), itemBeforeUpdate.getAvailable(), "Available not update");
        assertEquals(updateItem.getDescription(), itemBeforeUpdate.getDescription(), "Description not update");
    }

    @Test
    void updateItem_incorrectItem_throwsException() {
        ItemDto updateItem = itemDto1;
        updateItem.setAvailable(false);
        updateItem.setDescription("Update");
        updateItem.setName("Update");

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.updateItem(itemDto1.getId(), updateItem, user1.getId()));
    }
}
