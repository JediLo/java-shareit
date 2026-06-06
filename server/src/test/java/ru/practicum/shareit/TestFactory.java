package ru.practicum.shareit;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.item.ItemBaseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class TestFactory {
    public static User createUserById(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setName("User Name");
        return user;
    }

    public static Item createItemById(Long itemId, User owner) {
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);
        return item;
    }

    public static BookingDto createBookingDto(Long itemId) {
        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(now.plusMinutes(1));
        bookingDto.setEnd(now.plusMinutes(2));
        return bookingDto;
    }

    public static Booking createBooking(Item item, User booker, BookingStatus bookingStatus) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(bookingStatus);
        return booking;
    }

    public static Comment createComment(Long commentId, User author, String text) {
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setAuthor(author);
        comment.setText(text);
        return comment;
    }

    public static ItemRequest createItemRequest(String description, User requestor, Long requestId) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(description);
        itemRequest.setRequestor(requestor);
        itemRequest.setId(requestId);
        return itemRequest;
    }

    public static ItemBaseDto createItemBaseDto(String description, String name, Long requestId, Boolean available) {
        ItemBaseDto itemBaseDto = new ItemBaseDto();
        itemBaseDto.setDescription(description);
        itemBaseDto.setName(name);
        itemBaseDto.setRequestId(requestId);
        itemBaseDto.setAvailable(available);
        return itemBaseDto;
    }

    public static ItemRequestDto createItemRequestDto(String description, Long requesterId) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription(description);
        itemRequestDto.setRequesterId(requesterId);
        return itemRequestDto;
    }
}
