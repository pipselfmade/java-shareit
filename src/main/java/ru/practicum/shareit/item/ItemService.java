package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto getItem(Long itemId, Long userId);

    List<ItemDto> searchItem(String text, Long userId);

    List<ItemDto> getAllItems(Long userId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    ItemDto deleteItem(Long itemId, Long userId);
}
