package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapperDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestResponseDto addRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> userNotFound(userId));
        ItemRequest itemRequest = ItemRequestMapperDto.toItemRequest(itemRequestDto, user);
        ItemRequest saved = itemRequestRepository.save(itemRequest);
        return getItemRequestResponseDto(saved);
    }

    @Override
    public Collection<ItemRequestResponseDto> findRequestsByUserId(Long userId, Integer from, Integer size) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> userNotFound(userId));
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        return itemRequestRepository.findByRequestorId(userId, pageable)
                .getContent()
                .stream()
                .map(this::getItemRequestResponseDto)
                .toList();
    }

    @Override
    public Collection<ItemRequestResponseDto> findAllRequests(Long userId, Integer from, Integer size) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> userNotFound(userId));
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));

        return itemRequestRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(this::getItemRequestResponseDto)
                .toList();
    }

    @Override
    public ItemRequestResponseDto findRequestById(Long userId, Long requestId) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> userNotFound(userId));
        ItemRequest itemRequest = itemRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с Id " + requestId + " не найден"));
        return getItemRequestResponseDto(itemRequest);
    }


    private ItemRequestResponseDto getItemRequestResponseDto(ItemRequest itemRequest) {
        Collection<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());
        Collection<ItemShortDto> itemShorts = items.stream().map(ItemShortDto::new).toList();
        return ItemRequestMapperDto.toItemRequestResponseDto(itemRequest, itemShorts);
    }

    private NotFoundException userNotFound(Long userId) {
        return new NotFoundException("Пользователь c ID " + userId + " не найден");
    }

}
