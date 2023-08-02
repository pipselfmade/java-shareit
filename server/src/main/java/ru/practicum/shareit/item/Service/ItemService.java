package ru.practicum.shareit.item.Service;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(Long userId, Integer from, Integer size);

    ItemDto getItemById(Long userId, Long itemId);

    List<ItemDto> getItemsText(String text, Integer from, Integer size);

    ItemDto createItem(Long userId, ItemDto itemDto);

    public CommentDto createComment(CommentDto commentDto, Long userId, Long itemId);

    ItemDto updateItemById(Long userId, Long id, ItemDto item) throws CloneNotSupportedException;

    void deleteItemById(Long id);

    Long returnId();

    void setId(Long id);
}
