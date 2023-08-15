package ru.practicum.shareit.booking.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingEntity;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.Service.ItemServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.Service.UserServiceImpl;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    private BookingService bookingService;
    private final EasyRandom easyRandom = new EasyRandom();
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserServiceImpl userService;
    @Mock
    ItemServiceImpl itemService;
    Booking booking;
    BookingEntity bookingEntity;
    User user;
    Item item;

    @BeforeEach
    public void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService,
                itemService);
        booking = easyRandom.nextObject(Booking.class);
        bookingEntity = easyRandom.nextObject(BookingEntity.class);
        user = easyRandom.nextObject(User.class);
        item = easyRandom.nextObject(Item.class);
    }

    @Test
    void createBooking() throws BadRequestException {
        item.setAvailable(true);
        when(userService.returnId()).thenReturn(user.getId());
        bookingEntity.setItemId(item.getId());
        when(itemService.returnId()).thenReturn(item.getId());
        when(userRepository.getById(anyLong())).thenReturn(user);
        when(itemRepository.getById(anyLong())).thenReturn(item);
        BookingDto checkBookingDto = bookingService.createBooking(user.getId(), bookingEntity);
        assertEquals(checkBookingDto.getItem().getId(), bookingEntity.getItemId());
    }

    @Test
    void createBookingException() throws BadRequestException {
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(1L, bookingEntity));
    }

    @Test
    void createBookingException2() throws BadRequestException {
        userService.setId(1L);
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(1L, bookingEntity));
    }

    @Test
    void bookingStatus() throws BadRequestException {
        booking.setStatus(BookingStatus.WAITING);
        item.setOwner(user);
        booking.setItem(item);
        when(bookingRepository.getById(anyLong())).thenReturn(booking);
        BookingDto checkBookingDto = bookingService.bookingStatus(user.getId(), booking.getId(), true);
        assertEquals(checkBookingDto.getId(), booking.getId());
    }

    @Test
    void bookingStatusException() throws BadRequestException {
        booking.setStatus(BookingStatus.WAITING);
        item.setOwner(user);
        booking.setItem(item);
        when(bookingRepository.getById(anyLong())).thenReturn(booking);
        assertThrows(NotFoundException.class, () -> bookingService.bookingStatus(3L, booking.getId(), true));
    }

    @Test
    void bookingStatusException2() throws BadRequestException {
        booking.setStatus(BookingStatus.APPROVED);
        item.setOwner(user);
        booking.setItem(item);
        when(bookingRepository.getById(anyLong())).thenReturn(booking);
        assertThrows(BadRequestException.class, () -> bookingService.bookingStatus(user.getId(), booking.getId(), true));
    }

    @Test
    void getBooking() {
        when(bookingRepository.getById(anyLong())).thenReturn(booking);
        item.setOwner(user);
        booking.setItem(item);
        booking.setBooker(user);
        when(userService.returnId()).thenReturn(user.getId());
        BookingDto checkBookingDto = bookingService.getBooking(user.getId(), booking.getId());
        assertEquals(checkBookingDto.getId(), booking.getId());
    }

    @Test
    void getBookingException() {
        when(bookingRepository.getById(anyLong())).thenReturn(booking);
        item.setOwner(user);
        booking.setItem(item);
        booking.setBooker(user);
        when(userService.returnId()).thenReturn(1L);
        assertThrows(NotFoundException.class, () -> bookingService.getBooking(5L, booking.getId()));
    }

    @Test
    void getBookingException2() {
        bookingService.setId(1L);
        when(bookingRepository.getById(anyLong())).thenReturn(booking);
        item.setOwner(user);
        booking.setItem(item);
        booking.setBooker(user);
        when(userService.returnId()).thenReturn(user.getId());
        assertThrows(NotFoundException.class, () -> bookingService.getBooking(user.getId(), 5L));
    }

    @Test
    void getBookingException3() {
        bookingService.setId(1L);
        when(bookingRepository.getById(anyLong())).thenReturn(booking);
        when(userService.returnId()).thenReturn(user.getId());
        assertThrows(NotFoundException.class, () -> bookingService.getBooking(user.getId(), booking.getId()));
    }

    @Test
    void getBookingsOwner() throws BadRequestException {
        when(userService.returnId()).thenReturn(user.getId());
        when(bookingRepository.findAllByBookerOwnerIdOrderByDesc(anyLong(), any())).thenReturn(List.of(booking));
        List<Booking> bookingList = new ArrayList<>(Arrays.asList(booking));
        List<BookingDto> checkList = bookingService.getBookingsOwner(user.getId(), "ALL", 0, 10);
        assertEquals(checkList.size(), bookingList.size());
    }

    @Test
    void getBookingsOwnerException() throws BadRequestException {
        when(userService.returnId()).thenReturn(1L);
        assertThrows(NotFoundException.class, () -> bookingService.getBookingsOwner(3L, "ALL", 0, 10));
    }

    @Test
    void getBookingsOwnerException2() throws BadRequestException {
        when(userService.returnId()).thenReturn(user.getId());
        List<Booking> bookingList = new ArrayList<>(Arrays.asList(booking));
        assertThrows(BadRequestException.class, () -> bookingService.getBookingsOwner(user.getId(), "UNSUPPORTED_STATUS", 0, 10));
    }

    @Test
    void getBookingState() throws BadRequestException {
        when(userService.returnId()).thenReturn(user.getId());
        when(bookingRepository.findAllByBookerIdOrderByDesc(anyLong(), any())).thenReturn(List.of(booking));
        List<Booking> bookingList = new ArrayList<>(Arrays.asList(booking));
        List<BookingDto> checkList = bookingService.getBookingState(user.getId(), "ALL", 0, 10);
        assertEquals(checkList.size(), bookingList.size());
    }
}