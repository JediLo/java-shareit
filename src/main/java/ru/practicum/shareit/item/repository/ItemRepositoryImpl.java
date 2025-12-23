package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> storage = new LinkedHashMap<>();
    private long nextId = 1;

    @Override
    public Item save(Item item) {
        item.setId(nextId);
        storage.put(nextId, item);
        nextId++;
        return item;
    }


    @Override
    public void deleteByItemId(Long itemId) {
        storage.remove(itemId);
    }

    @Override
    public Collection<Item> findAllItemsUser(Long userId) {
        return storage.values()
                .stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item findByItemId(Long itemId) {
        return storage.get(itemId);
    }

    @Override
    public Collection<Item> findItemByText(String text) {
        return storage.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

}
