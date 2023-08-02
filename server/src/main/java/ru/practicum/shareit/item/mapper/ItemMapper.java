package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ItemMapper {
    public static Item makeItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public static ItemDto makeItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());

        if (item.getRequestId() != null) {
            itemDto.setRequestId(item.getRequestId().getId());
        }
        return itemDto;
    }

    public static List<ItemDto> listToItemDto(List<Item> item) throws BadRequestException {
        List<ItemDto> dtos = new ArrayList<>();

        for (Item request : item) {
            dtos.add(makeItemDto(request));
        }
        return dtos;
    }
}
