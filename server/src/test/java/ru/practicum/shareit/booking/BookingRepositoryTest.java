package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    BookingRepository bookingRepository;

    @BeforeEach
    public void saveObject() {
        User user = new User(1L, "name", "user@user.ru");
        userRepository.save(user);
        ItemRequest itemRequest = new ItemRequest(1L, "name", user, LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
        Item item1 = new Item(1L, user, "name", "description", true, itemRequest);
        itemRepository.save(item1);
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(2), item1, user, BookingStatus.APPROVED);
        bookingRepository.save(booking);
    }

    @AfterEach
    public void clearRepository() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void findAllByBookerIdOrderByDesc() {
        List<Booking> list = bookingRepository.findAllByBookerIdOrderByDesc(1L, Pageable.ofSize(1));
        assertEquals(1, list.size());
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfterOrderByDesc() {
        List<Booking> list = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByDesc(1L, LocalDateTime.now(),
                LocalDateTime.now().plusDays(1), Pageable.ofSize(1));
        assertEquals(1, list.size());
    }

    @Test
    void findAllByBookerIdAndEndBeforeOrderByDesc() {
        List<Booking> list = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByDesc(1L,
                LocalDateTime.now().plusDays(3), Pageable.ofSize(1));
        assertEquals(1, list.size());
    }

    @Test
    void findAllByBookerIdAndStartIsAfterOrderByStartDesc() {
        List<Booking> list = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(1L,
                LocalDateTime.now().minusDays(3), Pageable.ofSize(1));
        assertEquals(1, list.size());
    }

    @Test
    void findAllByBookerIdAndBookerStatusWaitingOrderByDesc() {
        Booking booking = bookingRepository.getOne(1L);
        booking.setStatus(BookingStatus.WAITING);
        List<Booking> list = bookingRepository.findAllByBookerIdAndBookerStatusWaitingOrderByDesc(1L,
                BookingStatus.WAITING, Pageable.ofSize(1));
        assertEquals(1, list.size());
    }

    @Test
    void findAllByBookerIdAndBookerStatusRejectedOrderByDesc() {
        Booking booking = bookingRepository.getOne(1L);
        booking.setStatus(BookingStatus.REJECTED);
        List<Booking> list = bookingRepository.findAllByBookerIdAndBookerStatusWaitingOrderByDesc(1L,
                BookingStatus.REJECTED, Pageable.ofSize(1));
        assertEquals(1, list.size());
    }

    @Test
    void findAllByBookerOwnerIdOrderByDesc() {
        List<Booking> list = bookingRepository.findAllByBookerOwnerIdOrderByDesc(1L, Pageable.ofSize(1));
        assertEquals(1, list.size());
    }

    @Test
    void findAllByBookerOwnerIdAndStartBeforeAndEndAfterOrderByDesc() {
        List<Booking> list = bookingRepository.findAllByBookerOwnerIdAndStartBeforeAndEndAfterOrderByDesc(1L, LocalDateTime.now(),
                LocalDateTime.now().plusDays(1), Pageable.ofSize(1));
        assertEquals(1, list.size());
    }

    @Test
    void findAllByBookerOwnerIdAndEndBeforeOrderByDesc() {
        List<Booking> list = bookingRepository.findAllByBookerOwnerIdAndEndBeforeOrderByDesc(1L,
                LocalDateTime.now().plusDays(3), Pageable.ofSize(1));
        assertEquals(1, list.size());
    }

    @Test
    void findAllByBookerOwnerIdAndBookerStartAfterOrderByDesc() {
        List<Booking> list = bookingRepository.findAllByBookerOwnerIdAndBookerStartAfterOrderByDesc(1L,
                LocalDateTime.now().minusDays(3), Pageable.ofSize(1));
        assertEquals(1, list.size());
    }

    @Test
    void findAllByBookerOwnerIdAndBookerStatusWaitingOrderByDesc() {
        Booking booking = bookingRepository.getOne(1L);
        booking.setStatus(BookingStatus.WAITING);
        List<Booking> list = bookingRepository.findAllByBookerOwnerIdAndBookerStatusWaitingOrderByDesc(1L,
                BookingStatus.WAITING, Pageable.ofSize(1));
        assertEquals(1, list.size());
    }

    @Test
    void findAllByBookerOwnerIdAndBookerStatusRejectedOrderByDesc() {
        Booking booking = bookingRepository.getOne(1L);
        booking.setStatus(BookingStatus.REJECTED);
        List<Booking> list = bookingRepository.findAllByBookerOwnerIdAndBookerStatusRejectedOrderByDesc(1L,
                BookingStatus.REJECTED, Pageable.ofSize(1));
        assertEquals(1, list.size());
    }

    @Test
    void checkStatusOfBooking() {
        assertEquals(BookingStatus.APPROVED, bookingRepository.checkStatusOfBooking(1L, 1L,
                LocalDateTime.now().plusDays(3)));
    }
}