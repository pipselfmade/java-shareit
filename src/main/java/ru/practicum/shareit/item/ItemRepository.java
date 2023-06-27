package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item createItem(Item item, User user);

    Optional<Item> getItem(Long itemId, Long userId);

    List<Item> searchItem(String text, Long userId);

    List<Item> getAllItems(Long userId);

    Item updateItem(Item item, Long itemId, Long userId);

    Optional<Item> deleteItem(Long itemId, Long userId);
}
