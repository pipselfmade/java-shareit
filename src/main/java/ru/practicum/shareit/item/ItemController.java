package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ru.practicum.shareit.item.ItemDto createItem(@RequestBody @Valid ru.practicum.shareit.item.ItemDto itemDto,
                                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.createItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ru.practicum.shareit.item.ItemDto getItem(@PathVariable @Valid Long itemId,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getItem(itemId, userId);
    }

    @GetMapping("/search")
    public List<ru.practicum.shareit.item.ItemDto> searchItem(@RequestParam String text,
                                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.searchItem(text, userId);
    }

    @GetMapping
    public List<ru.practicum.shareit.item.ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getAllItems(userId);
    }

    @PatchMapping("/{itemId}")
    public ru.practicum.shareit.item.ItemDto updateItem(@RequestBody ru.practicum.shareit.item.ItemDto itemDto,
                                                        @PathVariable Long itemId,
                                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.updateItem(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public ru.practicum.shareit.item.ItemDto deleteItem(@PathVariable Long itemId,
                                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.deleteItem(itemId, userId);
    }
}
