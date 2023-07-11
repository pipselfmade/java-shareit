package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    @Override
    public Item createItem(Item item, User user) {
        if (item.getId() == null) {
            item.setId(++id);
        }

        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> getItem(Long itemId, Long userId) {
        if (!items.containsKey(itemId)) {
            return Optional.empty();
        }

        return Optional.of(items.get(itemId));
    }

    @Override
    public List<Item> searchItem(String text, Long userId) {
        List<Item> itemList = new ArrayList<>();

        if (text.isEmpty()) {
            return itemList;
        }

        boolean nameContainsText;
        boolean descriptionContainsText;

        for (Item item : items.values()) {
            nameContainsText = item.getName().toLowerCase().contains(text.toLowerCase());
            descriptionContainsText = item.getDescription().toLowerCase().contains(text.toLowerCase());

            if ((nameContainsText || descriptionContainsText) && item.getAvailable()) {
                itemList.add(item);
            }
        }

        return itemList;
    }

    @Override
    public List<Item> getAllItems(Long userId) {
        List<Item> itemList = new ArrayList<>();

        for (Item item : items.values()) {
            if (item.getOwner().getId().equals(userId)) {
                itemList.add(item);
            }
        }

        return itemList;
    }

    @Override
    public Item updateItem(Item item, Long itemId, Long userId) {
        items.put(itemId, item);
        return getItem(itemId, userId).get();
    }

    @Override
    public Optional<Item> deleteItem(Long itemId, Long userId) {
        Optional<Item> memoryItem = getItem(itemId, userId);

        if (memoryItem.isEmpty()) {
            return Optional.empty();
        }

        items.remove(itemId);
        return memoryItem;
    }
}
