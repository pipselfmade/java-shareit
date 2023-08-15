package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select bok " +
            "from Booking as bok " +
            "where bok.booker.id = :id " +
            "order by bok.start desc")
    List<Booking> findAllByBookerIdOrderByDesc(Long id, Pageable pageable);

    @Query("select bok " +
            "from Booking as bok " +
            "where bok.booker.id = :id " +
            "and bok.start < :start and bok.end > :end " +
            "order by bok.id asc")
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByDesc(Long id,
                                                                        LocalDateTime start,
                                                                        LocalDateTime end,
                                                                        Pageable pageable);

    @Query("select bok " +
            "from Booking as bok " +
            "where bok.booker.id = :id " +
            "and bok.end < :end " +
            "order by bok.start desc")
    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByDesc(Long id, LocalDateTime end, Pageable pageable);

    @Query("select bok " +
            "from Booking as bok " +
            "where bok.booker.id = :id " +
            "and bok.start > :now " +
            "order by bok.start desc")
    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long id, LocalDateTime now, Pageable pageable);

    @Query("select bok " +
            "from Booking as bok " +
            "where bok.booker.id = :id " +
            "and (bok.status like :status) " +
            "order by bok.start desc")
    List<Booking> findAllByBookerIdAndBookerStatusWaitingOrderByDesc(Long id, BookingStatus status, Pageable pageable);

    @Query("select bok " +
            "from Booking as bok " +
            "where bok.booker.id = :id " +
            "and (bok.status like :status) " +
            "order by bok.start desc")
    List<Booking> findAllByBookerIdAndBookerStatusRejectedOrderByDesc(Long id, BookingStatus status, Pageable pageable);

    @Query("select bok " +
            "from Booking as bok " +
            "where bok.item.owner.id = :id " +
            "order by bok.start desc")
    List<Booking> findAllByBookerOwnerIdOrderByDesc(Long id, Pageable pageable);

    @Query("select bok " +
            "from Booking as bok " +
            "where bok.item.owner.id = :id " +
            "and bok.start < :start and bok.end > :end " +
            "order by bok.id asc")
    List<Booking> findAllByBookerOwnerIdAndStartBeforeAndEndAfterOrderByDesc(Long id,
                                                                             LocalDateTime start,
                                                                             LocalDateTime end,
                                                                             Pageable pageable);

    @Query("select bok " +
            "from Booking as bok " +
            "where bok.item.owner.id = :id " +
            "and bok.end < :end " +
            "order by bok.start desc")
    List<Booking> findAllByBookerOwnerIdAndEndBeforeOrderByDesc(Long id, LocalDateTime end, Pageable pageable);

    @Query("select bok " +
            "from Booking as bok " +
            "where bok.item.owner.id = :id " +
            "and bok.start > :now " +
            "order by bok.start desc")
    List<Booking> findAllByBookerOwnerIdAndBookerStartAfterOrderByDesc(Long id, LocalDateTime now, Pageable pageable);

    @Query("select bok " +
            "from Booking as bok " +
            "where bok.item.owner.id = :id " +
            "and bok.status like :status " +
            "order by bok.start desc")
    List<Booking> findAllByBookerOwnerIdAndBookerStatusWaitingOrderByDesc(Long id,
                                                                          BookingStatus status,
                                                                          Pageable pageable);

    @Query("select bok " +
            "from Booking as bok " +
            "where bok.item.owner.id = :id " +
            "and (bok.status like :status) " +
            "order by bok.start desc")
    List<Booking> findAllByBookerOwnerIdAndBookerStatusRejectedOrderByDesc(Long id,
                                                                           BookingStatus status,
                                                                           Pageable pageable);

    @Query(value = "select * " +
            "from Bookings as bok " +
            "where bok.item_id = :itemId " +
            "and bok.start_date > :now " +
            "and bok.status = 'APPROVED' " +
            "order by bok.start_date asc " +
            "limit 1", nativeQuery = true)
    Optional<Booking> getNextBookingForItem(Long itemId, LocalDateTime now);

    @Query(value = "select * " +
            "from Bookings as bok " +
            "where bok.item_id = :itemId " +
            "and bok.start_date < :now " +
            "and bok.status = 'APPROVED' " +
            "order by bok.start_date desc " +
            "limit 1", nativeQuery = true)
    Optional<Booking> getLastBookingForItem(Long itemId, LocalDateTime now);

    @Query(value = "select bok.status " +
            "from bookings as bok " +
            "where bok.id = :bookerId and " +
            "bok.item_id = :itemId and " +
            "bok.end_date < :localDateTime " +
            "limit 1 ", nativeQuery = true)
    BookingStatus checkStatusOfBooking(Long bookerId, Long itemId, LocalDateTime localDateTime);
}
