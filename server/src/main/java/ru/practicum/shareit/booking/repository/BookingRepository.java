package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBookerIdAndEndBefore(Long userId, LocalDateTime time, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatus(Long userId, BookingStatus state, Pageable pageable);

    Page<Booking> findAllByBookerId(Long userId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long userId,
                                                             LocalDateTime start,
                                                             LocalDateTime end,
                                                             Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartAfter(Long userId, LocalDateTime time, Pageable pageable);

    Page<Booking> findAllByItemOwnerId(Long userId, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndEndBefore(Long userId, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(Long userId,
                                                                LocalDateTime start,
                                                                LocalDateTime end,
                                                                Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartAfter(Long userId, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStatus(Long userId, BookingStatus status, Pageable pageable);

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
