package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.Service.UserService;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.item.mapper.ItemMapper.listToItemDto;
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.*;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private Long id = 0L;

    @Override
    public List<ItemRequestDto> getRequests(Long userId) {
        if (userId > userService.returnId()) {
            throw new NotFoundException("Указанного пользователя не существует(ItemRequestServiceImpl.getRequests)");
        }

        List<ItemRequestDto> itemRequestDto = listToItemRequestDto(itemRequestRepository.getAllItemRequestForUser(userId));

        for (ItemRequestDto dto : itemRequestDto) {
            List<Item> items = itemRepository.findAllItemWhereRequester(dto.getId());
            dto.setItems(listToItemDto(items));
        }
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getRequestsFrom(Long userId, Integer from, Integer size) {
        if (userId > userService.returnId()) {
            throw new NotFoundException("Указанного пользователя не существует(ItemRequestServiceImpl.getRequestsById)");
        }
        if (from < 0 || size <= 0) {
            throw new BadRequestException("Значения для страницы переданы не верно(ItemRequestServiceImpl.getRequestsById)");
        }

        int page = Math.round((float) from / size);
        Pageable pageable = PageRequest.of(page, size).withSort(Sort.by("id").descending());
        List<ItemRequestDto> itemRequestDto = listToItemRequestDto(itemRequestRepository
                .getAllItemRequestForUserNull(userId, pageable));

        for (ItemRequestDto dto : itemRequestDto) {
            List<Item> items = itemRepository.findAllItemWhereRequester(dto.getId());
            dto.setItems(listToItemDto(items));
        }
        return itemRequestDto;
    }

    @Override
    public ItemRequestDto getRequestsById(Long userId, Long requestId) {
        if (userId > userService.returnId()) {
            throw new NotFoundException("Указанного пользователя не существует(ItemRequestServiceImpl.getRequestsById)");
        }
        if (requestId > id) {
            throw new NotFoundException("Указанного запроса не существует(ItemRequestServiceImpl.getRequestsById)");
        }

        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(null);
        ItemRequestDto itemRequestDto = makeItemRequestDto(itemRequest);
        itemRequestDto.setItems(listToItemDto(itemRepository.findAllItemWhereRequester(requestId)));
        return itemRequestDto;
    }

    @Override
    public ItemRequestDto createRequests(Long userId, ItemRequestDto itemRequestDto) {
        if (userId > userService.returnId()) {
            throw new NotFoundException("Указанного пользователя не существует(ItemRequestServiceImpl.create)");
        }
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().equals("")) {
            throw new BadRequestException("Описание пустое (ItemRequestServiceImpl.create)");
        }

        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = makeItemRequest(itemRequestDto);
        itemRequest.setId(makeId());
        itemRequest.setRequester(userRepository.getById(userId));
        itemRequestRepository.save(itemRequest);
        itemRequestDto = makeItemRequestDto(itemRequest);
        return itemRequestDto;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Long makeId() {
        id += 1;
        return id;
    }
}
