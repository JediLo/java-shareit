package ru.practicum.shareit.item.dto.item;


import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class ItemMapperDto {

    public static ItemBaseDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }
        return new ItemBaseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getOwner() != null ? item.getOwner().getId() : null,
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Item toItem(ItemBaseDto itemBaseDto, User owner, ItemRequest request) {
        if (itemBaseDto == null) {
            return null;
        }
        return new Item(itemBaseDto.getName(),
                itemBaseDto.getDescription(),
                owner, request,
                itemBaseDto.getAvailable());
    }
}
