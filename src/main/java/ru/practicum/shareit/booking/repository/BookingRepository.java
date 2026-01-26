package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime time);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus state);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId,
                                                                             LocalDateTime start,
                                                                             LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime time);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId,
                                                                                LocalDateTime start,
                                                                                LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(Long id,
                                                                           BookingStatus approved,
                                                                           LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(Long id,
                                                                             BookingStatus approved,
                                                                             LocalDateTime now);

    boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId,
                                                           Long userId,
                                                           BookingStatus bookingStatus,
                                                           LocalDateTime now);
}
