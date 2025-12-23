package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Map<Long, Item>> items = new HashMap<>();

    @Override
    public Item save(Long userId, Item item) {
        item.setId(getNextItemId());
        if (items.containsKey(userId)) {
            items.get(userId).put(item.getId(), item);
            return item;
        }
        Map<Long, Item> itemsByUser = new HashMap<>();
        itemsByUser.put(item.getId(), item);
        items.put(userId, itemsByUser);
        return item;
    }


    @Override
    public void deleteByUserIdAndItemId(Long userId, Long itemId) {
        Map<Long, Item> itemsByUser = items.get(userId);
        if (itemsByUser == null) {
            return;
        }
        itemsByUser.remove(itemId);
    }

    @Override
    public Collection<Item> findAllItemsUser(Long userId) {
        Map<Long, Item> itemsByUser = items.get(userId);
        if (itemsByUser == null) {
            return Collections.emptyList();
        }
        return itemsByUser.values();
    }

    @Override
    public Item findByItemId(Long itemId) {
        return items.values().stream()
                .flatMap(userItems -> userItems.values().stream())
                .filter(item -> Objects.equals(item.getId(), itemId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Collection<Item> findItemByText(String text) {
        return items.values().stream()
                .flatMap(userItems -> userItems.values().stream())
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }


    private Long getNextItemId() {
        return items.values().stream()
                .flatMap(userItems -> userItems.values().stream())
                .mapToLong(Item::getId)
                .max()
                .orElse(0) + 1;
    }
}
