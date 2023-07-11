package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemDto;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto createItem(@RequestBody @Valid ItemDto itemDto,
                                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.createItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable @Valid Long itemId,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getItem(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text,
                                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.searchItem(text, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getAllItems(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                                                        @PathVariable Long itemId,
                                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.updateItem(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public ItemDto deleteItem(@PathVariable Long itemId,
                                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.deleteItem(itemId, userId);
    }
}
