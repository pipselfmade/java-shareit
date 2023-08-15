package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET request userId: {}", userId);
        return itemRequestService.getRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequestsFrom(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "5") Integer size) {
        log.info("GET all requests with userId: {}", userId);
        return itemRequestService.getRequestsFrom(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestsById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long requestId) {
        log.info("GET request with userId and requestId: {}, {}", userId, requestId);
        return itemRequestService.getRequestsById(userId, requestId);
    }

    @PostMapping
    public ItemRequestDto createRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST request to add an item request with userId: {}", userId);
        return itemRequestService.createRequests(userId, itemRequestDto);
    }
}
