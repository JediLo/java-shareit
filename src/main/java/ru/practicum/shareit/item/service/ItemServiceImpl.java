package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapperDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Collection<ItemDto> findAllItemsUser(Long userId) {
        validateUserId(userId);
        User user = userRepository.findUserByID(userId);
        validateUser(user);
        Collection<Item> items = itemRepository.findAllItemsUser(userId);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        return items.stream().map(ItemMapperDto::toItemDto).toList();
    }

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        validateUserId(userId);
        validateItemDto(itemDto);
        User user = userRepository.findUserByID(userId);
        validateUser(user);
        Item item = ItemMapperDto.toItem(itemDto, user, null);
        Item saved = itemRepository.save(userId, item);
        return ItemMapperDto.toItemDto(saved);
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        validateUserId(userId);
        validateItemId(itemId);
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }

    @Override
    public ItemDto findItemById(Long itemId) {
        validateItemId(itemId);
        Item item = itemRepository.findByItemId(itemId);
        if (item == null) {
            throw new NotFoundException("Предмет не найден");
        }
        return ItemMapperDto.toItemDto(item);
    }


    @Override
    public Collection<ItemDto> findItemByText(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        Collection<Item> items = itemRepository.findItemByText(text);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        return items.stream().map(ItemMapperDto::toItemDto).toList();
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId) {
        validateUserId(userId);
        validateItemDto(itemDto);
        validateItemId(itemId);

        User user = userRepository.findUserByID(userId);
        validateUser(user);
        Item item = itemRepository.findByItemId(itemId);
        if (item == null) {
            throw new NotFoundException("Вещь не найдена");
        }
        if (!item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Только хозяин вещи может редактировать вещь");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapperDto.toItemDto(item);
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new ValidationException("Не указан ID пользователя");
        }
    }

    private void validateItemId(Long itemId) {
        if (itemId == null) {
            throw new ValidationException("Не указан ID предмета");
        }
    }

    private void validateItemDto(ItemDto itemDto) {
        if (itemDto == null) {
            throw new ValidationException("Не переданы данные предмета");
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}
