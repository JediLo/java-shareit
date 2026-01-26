package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapperDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponseDto addBooking(BookingDto bookingDto, Long userId) {
        Item bookingItem = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));
        User user = userRepository.findById(userId).orElseThrow(() -> userNotFound(userId));
        if (!bookingItem.isAvailable()) {
            throw new ValidationException("Вещь недоступна");
        }
        if (bookingItem.getOwner().getId().equals(user.getId())) {
            throw new ValidationException("Нельзя забронировать свою вещь");
        }
        Booking booking = BookingMapperDto.toBooking(bookingDto, bookingItem, user);
        booking.setStatus(BookingStatus.WAITING);
        Booking saved = bookingRepository.save(booking);
        return BookingMapperDto.toBookingResponseDto(saved);
    }

    @Override
    @Transactional
    public BookingResponseDto addStatusBooking(boolean approved, Long bookingId, Long userId) {
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> bookingNotFound(bookingId));
        Item item = booking.getItem();
        if (!item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Только хозяин вещи может подтвердить или отменить заявку.");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("На этот предмет нет бронирования, подтвердить или отклонить нельзя");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapperDto.toBookingResponseDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> bookingNotFound(bookingId));
        if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new ValidationException("Запрос может выполнить хозяин вещи или хозяин брони");
        }
        return BookingMapperDto.toBookingResponseDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookingResponseDto> getBookingsByStateToBooker(Long userId, BookingState state) {
        userRepository.findById(userId).orElseThrow(() -> userNotFound(userId));

        LocalDateTime now = LocalDateTime.now();
        boolean owner = false;

        return getBookingByState(userId, state, owner, now).stream().map(BookingMapperDto::toBookingResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookingResponseDto> getBookingsByStateToOwner(Long userId, BookingState state) {

        userRepository.findById(userId).orElseThrow(() -> userNotFound(userId));

        LocalDateTime now = LocalDateTime.now();
        boolean owner = true;

        return getBookingByState(userId, state, owner, now).stream().map(BookingMapperDto::toBookingResponseDto)
                .toList();
    }


    private NotFoundException bookingNotFound(Long bookingId) {
        throw new NotFoundException("Бронирование c ID " + bookingId + " не найден");
    }

    private NotFoundException userNotFound(Long userId) {
        throw new NotFoundException("Пользователь c ID " + userId + " не найден");
    }

    private Collection<Booking> getBookingByState(Long userId,
                                                  BookingState state,
                                                  boolean owner,
                                                  LocalDateTime now) {
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");

        return switch (state) {
            case ALL -> owner ? bookingRepository.findAllByItemOwnerId(userId, sortByStartDesc)
                    : bookingRepository.findAllByBookerId(userId, sortByStartDesc);
            case PAST -> owner ?
                    bookingRepository.findAllByItemOwnerIdAndEndBefore(userId, now, sortByStartDesc)
                    : bookingRepository.findAllByBookerIdAndEndBefore(userId, now, sortByStartDesc);
            case CURRENT -> owner ?
                    bookingRepository
                            .findAllByItemOwnerIdAndStartBeforeAndEndAfter(userId, now, now, sortByStartDesc)
                    : bookingRepository
                    .findAllByBookerIdAndStartBeforeAndEndAfter(userId, now, now, sortByStartDesc);
            case FUTURE -> owner ?
                    bookingRepository.findAllByItemOwnerIdAndStartAfter(userId, now, sortByStartDesc)
                    : bookingRepository.findAllByBookerIdAndStartAfter(userId, now, sortByStartDesc);
            case WAITING, REJECTED -> {
                BookingStatus status = BookingStatus.valueOf(state.name());
                yield owner ?
                        bookingRepository.findAllByItemOwnerIdAndStatus(userId, status, sortByStartDesc)
                        : bookingRepository.findAllByBookerIdAndStatus(userId, status, sortByStartDesc);
            }
        };
    }

}


