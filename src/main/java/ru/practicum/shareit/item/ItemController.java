package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") Long itemId) {
        return itemService.findItemById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findAllItemsUser(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemByText(@RequestParam("text") String text) {
        return itemService.findItemByText(text);
    }

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable("itemId") Long itemId) {
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable("itemId") Long itemId) {
        itemService.deleteItem(userId, itemId);
    }

}