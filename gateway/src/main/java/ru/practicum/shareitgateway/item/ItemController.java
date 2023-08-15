package ru.practicum.shareitgateway.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private static final String HEADER = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(HEADER) Long userId,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("New GET /items request");
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsText(@RequestParam String text,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("New GET /items/search request");
        return itemClient.getItemsText(text, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@RequestHeader(HEADER) Long userId,
                                              @PathVariable Long id) {
        log.info("New GET /items/{} request", id);
        return itemClient.getItemById(userId, id);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(HEADER) Long userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        log.info("New POST /items request");
        return itemClient.createItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto commentDto,
                                                @RequestHeader(HEADER) Long userId,
                                                @PathVariable Long itemId) {
        log.info("New POST /items/{}/comment request", itemId);
        return itemClient.createComment(commentDto, userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateUserByIdPatch(@RequestHeader(HEADER) Long userId,
                                                      @PathVariable Long itemId,
                                                      @Valid @RequestBody ItemDto item) {
        log.info("New PATCH /items/{} request", itemId);
        return itemClient.updateItemById(userId, itemId, item);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUserById(@PathVariable Long id) {
        log.info("New DELETE /items/{} request", id);
        return itemClient.deleteItemById(id);
    }
}
