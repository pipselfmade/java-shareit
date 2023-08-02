package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    List<ItemRequestDto> getRequests(Long userId);

    List<ItemRequestDto> getRequestsFrom(Long userId, Integer from, Integer size);

    ItemRequestDto getRequestsById(Long userId, Long requestId);

    ItemRequestDto createRequests(Long userId, ItemRequestDto itemRequestDto);

    void setId(Long id);
}
