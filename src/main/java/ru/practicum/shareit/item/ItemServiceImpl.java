package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        Optional<User> optionalUser = userRepository.getUser(userId);
        Item item = ItemMapper.toObject(itemDto);

        if (optionalUser.isEmpty()) {
            log.info("User with id " + userId + " not found");
            throw new NotFoundException("User with id " + userId + " not found");
        }

        item.setOwner(optionalUser.get());
        log.info("Item " + item.getName() + " created successfully");
        return ItemMapper.toDto(itemRepository.createItem(item, optionalUser.get()));
    }

    @Override
    public ItemDto getItem(Long itemId, Long userId) {
        Optional<Item> optionalItem = itemRepository.getItem(itemId, userId);

        if (optionalItem.isEmpty()) {
            log.info("Item with id " + itemId + " not found for getting");
            throw new NotFoundException("Item with id " + itemId + " not found for getting");
        }

        log.info("Item with id " + itemId + " returned successfully");
        return ItemMapper.toDto(optionalItem.get());
    }

    @Override
    public List<ItemDto> searchItem(String text, Long userId) {
        List<ItemDto> itemsDto = new ArrayList<>();

        for (Item item : itemRepository.searchItem(text, userId)) {
            itemsDto.add(ItemMapper.toDto(item));
        }

        log.info("Searched items returned successfully");
        return itemsDto;
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        List<ItemDto> itemsDto = new ArrayList<>();

        for (Item item : itemRepository.getAllItems(userId)) {
            itemsDto.add(ItemMapper.toDto(item));
        }

        log.info("All items returned successfully");
        return itemsDto;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        userRepository.getUser(userId);
        Item itemForUpdate = ItemMapper.toObject(getItem(itemId, userId));

        if (!itemForUpdate.getOwner().getId().equals(userId)) {
            log.info("Item with id " + itemId + " not found for getting");
            throw new NotFoundException("Item with id " + itemId + " not found for getting");
        }

        if (itemDto.getName() != null) {
            itemForUpdate.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            itemForUpdate.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            itemForUpdate.setAvailable(itemDto.getAvailable());
        }

        log.info("Item " + itemForUpdate.getName() + " updated successfully");
        return ItemMapper.toDto(itemRepository.updateItem(itemForUpdate, itemId, userId));
    }

    @Override
    public ItemDto deleteItem(Long itemId, Long userId) {
        userRepository.getUser(userId);
        Optional<Item> optionalItem = itemRepository.deleteItem(itemId, userId);

        if (optionalItem.isEmpty()) {
            log.info("Item with id " + itemId + " not found for deleting");
            throw new NotFoundException("Item with id " + itemId + " not found for deleting");
        }

        if (!optionalItem.get().getOwner().getId().equals(userId)) {
            log.info("Unable to update item , user does not have such item");
            throw new NotFoundException("Unable to update item , user does not have such item");
        }

        log.info("Item with id " + itemId + " deleted successfully");
        return ItemMapper.toDto(optionalItem.get());
    }
}
