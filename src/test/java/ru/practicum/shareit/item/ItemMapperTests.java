package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ItemMapperTests {
    @Test
    void toItemDto() {
        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .owner(User.builder().build())
                .description("Test item description")
                .available(true).build();
        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getOwner(), itemDto.getOwner());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertNull(itemDto.getRequestId());
    }

    @Test
    void toItem() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .owner(User.builder().build())
                .description("Test item description")
                .available(true).build();
        Item item = ItemMapper.toItem(itemDto);

        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getOwner(), item.getOwner());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
    }

    @Test
    void toItemForRequest() {
        Item item = Item.builder()
                .id(1L)
                .owner(User.builder().id(1L).build())
                .name("Test Item")
                .description("Test item description")
                .available(true).build();
        ItemForRequest itemForRequest = ItemMapper.toItemForRequest(item);

        assertEquals(item.getId(), itemForRequest.getId());
        assertEquals(item.getOwner().getId(), itemForRequest.getOwnerId());
        assertEquals(item.getName(), itemForRequest.getName());
        assertEquals(item.getDescription(), itemForRequest.getDescription());
        assertEquals(item.getAvailable(), itemForRequest.getAvailable());
        assertNull(itemForRequest.getRequestId());
    }
}
