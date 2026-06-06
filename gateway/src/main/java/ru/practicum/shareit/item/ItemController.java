package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") @NotNull long userId,
                                          @PathVariable("itemId") @NotNull long itemId) {
        log.info("Getting item with id {}", itemId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") @NotNull long userId,
                                           @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Getting items to userId {} with from {} and size {}", userId, from, size);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByText(@RequestParam("text") String text,
                                             @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Getting items by text {} with from {} and size {}", text, from, size);
        return itemClient.findByText(userId, text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody @NotNull ItemRequestDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.info("Creating item {}", itemDto.getName());
        return itemClient.createItem(userId, itemDto);

    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody @NotNull ItemRequestDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                             @PathVariable("itemId") @NotNull Long itemId) {
        log.info("Updating item {}", itemDto.getName());
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                             @PathVariable("itemId") @NotNull Long itemId) {
        log.info("Deleting item {}", itemId);
        return itemClient.deleteItem(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestBody @NotNull @Valid CommentRequestDto commentDto,
                                                @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                @PathVariable("itemId") @NotNull Long itemId) {
        log.info("create Comment {} ", commentDto);
        return itemClient.createComment(itemId, userId, commentDto);
    }
}
