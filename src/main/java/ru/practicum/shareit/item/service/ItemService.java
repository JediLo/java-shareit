package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;


public interface ItemService {

    ItemDto addNewItem(Long userId, ItemDto item);

    void deleteItem(Long userId, Long itemId);

    ItemDto findItemById(Long itemId);

    Collection<ItemDto> findAllItemsUser(Long userId);

    Collection<ItemDto> findItemByText(String text);

    ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId);
}
