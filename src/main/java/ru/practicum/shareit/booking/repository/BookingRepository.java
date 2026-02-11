package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdAndEndBefore(Long userId, LocalDateTime time, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(Long userId, BookingStatus state, Sort sort);

    List<Booking> findAllByBookerId(Long userId, Sort sort);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long userId,
                                                             LocalDateTime start,
                                                             LocalDateTime end,
                                                             Sort sort);

    List<Booking> findAllByBookerIdAndStartAfter(Long userId, LocalDateTime time, Sort sort);

    List<Booking> findAllByItemOwnerId(Long userId, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndBefore(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(Long userId,
                                                                LocalDateTime start,
                                                                LocalDateTime end,
                                                                Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartAfter(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatus(Long userId, BookingStatus status, Sort sort);

    Optional<Booking> findFirstByItemIdAndStatusAndEndBefore(Long id,
                                                             BookingStatus approved,
                                                             LocalDateTime now,
                                                             Sort sort);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfter(Long id,
                                                              BookingStatus approved,
                                                              LocalDateTime now,
                                                              Sort sort);

    boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId,
                                                           Long userId,
                                                           BookingStatus bookingStatus,
                                                           LocalDateTime now);
}
