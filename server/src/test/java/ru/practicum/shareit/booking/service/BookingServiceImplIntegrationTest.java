package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingEntity;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.Service.ItemServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.Service.UserServiceImpl;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BookingServiceImplIntegrationTest {
    @Autowired
    BookingService bookingService;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserServiceImpl userService;
    @Autowired
    ItemServiceImpl itemService;
    ItemRequest itemRequest;
    Item item;
    Item item2;
    User user;
    User user2;
    Booking booking;
    Booking booking2;

    @BeforeEach
    public void setUp() {
        user = new User(1L, "name", "user@user.ru");
        userRepository.save(user);
        user2 = new User(2L, "name2", "user2@user.ru");
        userRepository.save(user2);
        userService.setId(2L);
        item = new Item(1L, user, "name", "Description", true, itemRequest);
        itemRepository.save(item);
        item2 = new Item(2L, user2, "name2", "Description2", true, itemRequest);
        itemRepository.save(item2);
        booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(1), item, user,
                BookingStatus.WAITING);
        bookingRepository.save(booking);
        booking2 = new Booking(2L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(1), item2, user2,
                BookingStatus.WAITING);
        bookingRepository.save(booking2);
    }

    @Test
    @DirtiesContext
    void createBooking() throws BadRequestException {
        userService.setId(2L);
        itemService.setId(2L);
        BookingEntity bookingEntity = new BookingEntity(1L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(1), 2L);
        BookingDto bookingDto = bookingService.createBooking(1L, bookingEntity);
        assertEquals(bookingDto.getId(), bookingEntity.getId());
    }

    @Test
    @DirtiesContext
    void bookingStatus() throws BadRequestException {
        BookingDto bookingDto = bookingService.bookingStatus(1L, 1L, true);
        assertEquals(bookingDto.getId(), booking.getId());
        assertEquals(bookingDto.getStatus(), BookingStatus.APPROVED);
        BookingDto bookingDto1 = bookingService.bookingStatus(2L, 2L, false);
        assertEquals(bookingDto1.getId(), booking2.getId());
        assertEquals(bookingDto1.getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void getBooking() {
        bookingService.setId(2L);
        BookingDto bookingDto = bookingService.getBooking(1L, 1L);
        assertEquals(bookingDto.getId(), booking.getId());
    }

    @Test
    @DirtiesContext
    void getBookingsOwner() throws BadRequestException {
        List<BookingDto> listAll = bookingService.getBookingsOwner(1L, "ALL", 0, 10);
        assertEquals(listAll.size(), 1);
        List<BookingDto> listCurrent = bookingService.getBookingsOwner(1L, "CURRENT", 0, 10);
        assertEquals(listCurrent.size(), 1);
        booking.setStart(LocalDateTime.now().minusMinutes(10));
        booking.setEnd(LocalDateTime.now().minusMinutes(5));
        bookingRepository.save(booking);
        List<BookingDto> listPast = bookingService.getBookingsOwner(1L, "PAST", 0, 10);
        assertEquals(listPast.size(), 1);
        booking.setStart(LocalDateTime.now().plusMinutes(5));
        booking.setEnd(LocalDateTime.now().plusMinutes(10));
        bookingRepository.save(booking);
        List<BookingDto> listFuture = bookingService.getBookingsOwner(1L, "FUTURE", 0, 10);
        assertEquals(listFuture.size(), 1);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now());
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        List<BookingDto> listWaiting = bookingService.getBookingsOwner(1L, "WAITING", 0, 10);
        assertEquals(listWaiting.size(), 1);
        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);
        List<BookingDto> listRejected = bookingService.getBookingsOwner(1L, "REJECTED", 0, 10);
        assertEquals(listRejected.size(), 1);
    }

    @Test
    @DirtiesContext
    void getBookingState() throws BadRequestException {
        List<BookingDto> listAll = bookingService.getBookingState(1L, "ALL", 0, 10);
        assertEquals(listAll.size(), 1);
        List<BookingDto> listCurrent = bookingService.getBookingState(1L, "CURRENT", 0, 10);
        assertEquals(listCurrent.size(), 1);
        booking.setStart(LocalDateTime.now().minusMinutes(10));
        booking.setEnd(LocalDateTime.now().minusMinutes(5));
        bookingRepository.save(booking);
        List<BookingDto> listPast = bookingService.getBookingState(1L, "PAST", 0, 10);
        assertEquals(listPast.size(), 1);
        booking.setStart(LocalDateTime.now().plusMinutes(5));
        booking.setEnd(LocalDateTime.now().plusMinutes(10));
        bookingRepository.save(booking);
        List<BookingDto> listFuture = bookingService.getBookingState(1L, "FUTURE", 0, 10);
        assertEquals(listFuture.size(), 1);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now());
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        List<BookingDto> listWaiting = bookingService.getBookingState(1L, "WAITING", 0, 10);
        assertEquals(listWaiting.size(), 1);
        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);
        List<BookingDto> listRejected = bookingService.getBookingState(1L, "REJECTED", 0, 10);
        assertEquals(listRejected.size(), 1);
    }

    @Test
    void returnId() {
        assertEquals(0L, bookingService.returnId());
        bookingService.setId(2L);
        assertEquals(2L, bookingService.returnId());
    }
}