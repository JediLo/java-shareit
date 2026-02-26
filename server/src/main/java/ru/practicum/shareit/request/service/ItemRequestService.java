package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestResponseDto addRequest(Long userId, ItemRequestDto itemRequestDto);

    Collection<ItemRequestResponseDto> findRequestsByUserId(Long userId, Integer from, Integer size);

    Collection<ItemRequestResponseDto> findAllRequests(Long userId, Integer from, Integer size);

    ItemRequestResponseDto findRequestById(Long userId, Long requestId);
}
