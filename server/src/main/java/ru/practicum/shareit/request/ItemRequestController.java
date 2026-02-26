package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponseDto createNewRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestBody ItemRequestDto itemRequestDto) {

        return itemRequestService.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public Collection<ItemRequestResponseDto> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                          @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestService.findRequestsByUserId(userId, from, size);
    }

    @GetMapping("/all")
    public Collection<ItemRequestResponseDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                             @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                             @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestService.findAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable("requestId") Long requestId) {
        return itemRequestService.findRequestById(userId, requestId);
    }
}
