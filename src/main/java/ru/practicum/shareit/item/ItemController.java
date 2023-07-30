package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;

import javax.validation.Valid;
import javax.xml.bind.ValidationException;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long id) {
        return itemService.getItems(id);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam @DefaultValue(value = " ") String text,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "10") int size) throws ValidationException {
        return itemService.searchItems(text, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") @DefaultValue(value = "0L") Long id,
                              @RequestBody ItemDto itemDto,
                              @PathVariable Long itemId) {
        log.info("Обновление вещи id: {}", id);
        return itemService.updateItem(id, itemDto, itemId);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long id,
                           @Valid @RequestBody ItemDto itemDto) {
        log.info("Добавлена вещь: {}", itemDto);
        return itemService.addItem(id, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long id,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(id, itemId, commentDto);
    }
}
