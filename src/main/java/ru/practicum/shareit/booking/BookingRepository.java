package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatus(Long bookerId,
                                             Status status,
                                             Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndBefore(Long bookerId,
                                                LocalDateTime dateTime,
                                                Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartAfter(Long bookerId,
                                                 LocalDateTime dateTime,
                                                 Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long bookerId,
                                                             LocalDateTime dateTime,
                                                             LocalDateTime dateTime1,
                                                             Pageable pageable);

    List<Booking> findByItemId(Long itemId,
                               Sort sort);

    Boolean existsByBookerIdAndEndBeforeAndStatus(Long bookerId,
                                                  LocalDateTime localDateTime,
                                                  Status status);

    Page<Booking> findAllByItemOwnerId(Long ownerId,
                                       Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStatus(Long ownerId,
                                                Status status,
                                                Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndEndBefore(Long ownerId,
                                                   LocalDateTime dateTime,
                                                   Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartAfter(Long ownerId,
                                                    LocalDateTime dateTime,
                                                    Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(Long ownerId,
                                                                LocalDateTime dateTime,
                                                                LocalDateTime dateTime1,
                                                                Pageable pageable);

    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(Long itemId,
                                                                               LocalDateTime localDate,
                                                                               Status status);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId,
                                                                             LocalDateTime localDate,
                                                                             Status status);
}
