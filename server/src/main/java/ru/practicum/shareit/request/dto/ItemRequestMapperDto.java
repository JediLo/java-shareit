package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@UtilityClass
public class ItemRequestMapperDto {

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requestor) {
        return new ItemRequest(itemRequestDto.getDescription(), requestor);
    }

    public ItemRequestResponseDto toItemRequestResponseDto(ItemRequest itemRequest, Collection<ItemShortDto> itemShortDto) {
        return new ItemRequestResponseDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemShortDto);
    }
}
