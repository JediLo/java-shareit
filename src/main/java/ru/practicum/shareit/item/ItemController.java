package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.response.CommentResponseDto;
import ru.practicum.shareit.item.dto.item.ItemBaseDto;
import ru.practicum.shareit.item.dto.response.ItemResponseDto;
import ru.practicum.shareit.item.dto.response.ItemResponseMapperDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemById(@PathVariable("itemId") @NotNull Long itemId,
                                       @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemService.findItemById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemResponseDto> getItems(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemService.findAllItemsUser(userId);
    }

    @GetMapping("/search")
    public Collection<ItemResponseDto> getItemByText(@RequestParam("text") String text,
                                                     @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemService.findItemByText(text, userId);
    }

    @PostMapping
    public ItemResponseDto createItem(@Valid @RequestBody @NotNull ItemBaseDto itemBaseDto,
                                  @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemService.addNewItem(userId, itemBaseDto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@RequestBody @NotNull ItemBaseDto itemBaseDto,
                                  @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                  @PathVariable("itemId") @NotNull Long itemId) {
        return itemService.updateItem(userId, itemBaseDto, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                           @PathVariable("itemId") @NotNull Long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@RequestBody @NotNull @Valid CommentDto commentDto,
                                         @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                         @PathVariable("itemId") @NotNull Long itemId) {
        return itemService.addComment(commentDto, itemId, userId);
    }

}