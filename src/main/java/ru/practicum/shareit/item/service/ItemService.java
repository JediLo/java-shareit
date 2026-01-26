package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.response.CommentResponseDto;
import ru.practicum.shareit.item.dto.item.ItemBaseDto;
import ru.practicum.shareit.item.dto.response.ItemResponseDto;
import ru.practicum.shareit.item.dto.response.ItemResponseMapperDto;

import java.util.Collection;


public interface ItemService {

    ItemResponseDto addNewItem(Long userId, ItemBaseDto item);

    void deleteItem(Long userId, Long itemId);

    ItemResponseDto findItemById(Long itemId,  Long userId);

    Collection<ItemResponseDto> findAllItemsUser(Long userId);

    Collection<ItemResponseDto> findItemByText(String text, Long userId);

    ItemResponseDto updateItem(Long userId, ItemBaseDto itemBaseDto, Long itemId);

    CommentResponseDto addComment(CommentDto commentDto, Long itemId, Long userId);
}
