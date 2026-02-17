package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByBookerId(
            Long bookerId,
            Pageable pageable
    );

    Page<Booking> findByItemOwnerId(
            Long ownerId,
            Pageable pageable
    );

    Page<Booking> findByBookerIdAndEndDateIsBefore(
            Long bookerId,
            LocalDateTime end,
            Pageable pageable
    );

    Page<Booking> findByBookerIdAndStartDateIsAfter(
            Long bookerId,
            LocalDateTime start,
            Pageable pageable
    );

    Page<Booking> findByBookerIdAndStatus(
            Long bookerId,
            BookingStatus status,
            Pageable pageable
    );

    Collection<Booking> findByItemId(
            Long itemId
    );

    Collection<Booking> findByItemIdAndBookerIdAndEndDateIsBefore(
            Long itemId,
            Long bookerId,
            LocalDateTime end
    );

    Page<Booking> findByItemOwnerIdAndEndDateIsBefore(
            Long ownerId,
            LocalDateTime end,
            Pageable pageable
    );

    Page<Booking> findByItemOwnerIdAndStartDateIsAfter(
            Long ownerId,
            LocalDateTime start,
            Pageable pageable
    );

    Page<Booking> findByItemOwnerIdAndStatus(
            Long ownerId,
            BookingStatus status,
            Pageable pageable
    );

    Page<Booking> findByBookerIdAndStartDateIsBeforeAndEndDateIsAfter(
            Long bookerId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    Page<Booking> findByItemOwnerIdAndStartDateIsBeforeAndEndDateIsAfter(
            Long ownerId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );
}
