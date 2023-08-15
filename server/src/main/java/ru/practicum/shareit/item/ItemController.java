package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.Service.ItemService;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestParam(defaultValue = "0") Integer from,
                                  @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET items with userId: {}", userId);
        return itemService.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsText(@RequestParam String text,
                                      @RequestParam(defaultValue = "0") Integer from,
                                      @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET items with text: {}", text);
        return itemService.getItemsText(text, from, size);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long id) {
        log.info("GET item with userId and itemId: {}, {}", userId, id);
        return itemService.getItemById(userId, id);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto) {
        log.info("POST item with userId: {}", userId);
        return itemService.createItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto,
                                    @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId) {
        log.info("POST item comment with userId: {}", userId);
        return itemService.createComment(commentDto, userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateUserByIdPatch(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable Long itemId,
                                       @RequestBody ItemDto item) throws CloneNotSupportedException {
        log.info("PATCH item with userId and itemId: {}, {}", userId, itemId);
        return itemService.updateItemById(userId, itemId, item);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        log.info("DEL item with itemId: {}", id);
        itemService.deleteItemById(id);
    }
}
