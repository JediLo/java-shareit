package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.item.ItemBaseDto;
import ru.practicum.shareit.item.dto.item.response.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemById(@PathVariable("itemId") Long itemId,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findItemById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemResponseDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemService.findAllItemsUser(userId, from, size);
    }

    @GetMapping("/search")
    public Collection<ItemResponseDto> getItemByText(@RequestParam("text") String text,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemService.findItemByText(text, userId, from, size);
    }

    @PostMapping
    public ItemResponseDto createItem(@RequestBody ItemBaseDto itemBaseDto,
                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.addNewItem(userId, itemBaseDto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@RequestBody ItemBaseDto itemBaseDto,
                                      @RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable("itemId") Long itemId) {
        return itemService.updateItem(userId, itemBaseDto, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable("itemId") Long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@RequestBody CommentDto commentDto,
                                         @RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable("itemId") Long itemId) {
        return itemService.addComment(commentDto, itemId, userId);
    }

}