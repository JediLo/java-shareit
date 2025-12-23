package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;


public interface ItemRepository {

    Item save(Item item);

    void deleteByItemId(Long itemId);

    Collection<Item> findAllItemsUser(Long userId);

    Item findByItemId(Long itemId);

    Collection<Item> findItemByText(String text);

}