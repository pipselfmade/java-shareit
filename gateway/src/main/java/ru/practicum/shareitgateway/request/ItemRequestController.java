package ru.practicum.shareitgateway.request;

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
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String HEADER = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader(HEADER) Long userId) {
        log.info("New GET /requests request");
        return itemRequestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsFrom(@RequestHeader(HEADER) Long userId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "5") Integer size) {
        log.info("New GET /requests/all request");
        return itemRequestClient.getRequestsFrom(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestsById(@RequestHeader(HEADER) Long userId,
                                                  @PathVariable Long requestId) {
        log.info("New GET /requests/{} request", requestId);
        return itemRequestClient.getRequestsById(userId, requestId);
    }

    @PostMapping
    public ResponseEntity<Object> createRequests(@RequestHeader(HEADER) Long userId,
                                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("New POST /requests request");
        return itemRequestClient.createRequests(userId, itemRequestDto);
    }
}
