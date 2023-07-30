package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentDto;

import javax.xml.bind.ValidationException;
import java.util.List;

public interface ItemService {

    List<ItemDto> getItems(Long id);

    ItemDto getItemById(Long userId, Long itemId);

    List<ItemDto> searchItems(String text, int from, int size) throws ValidationException;

    ItemDto updateItem(Long id, ItemDto itemDto, Long itemId);

    ItemDto addItem(Long id, ItemDto itemDto);

    CommentDto addComment(Long id, Long itemId, CommentDto commentDto);

}