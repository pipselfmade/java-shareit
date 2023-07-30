package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotSupportedStateException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.item.ItemMapper.toItem;
import static ru.practicum.shareit.item.ItemMapper.toItemDto;
import static ru.practicum.shareit.user.UserMapper.toUser;
import static ru.practicum.shareit.user.UserMapper.toUserDto;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTests {
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    private User user;
    private User user1;

    private Item item;
    private Item item1;
    private Item item2;

    private Booking booking;
    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    public void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("test")
                .email("test@test.ru").build();
        user = toUser(userService.addUser(toUserDto(user)));
        item = Item.builder()
                .id(1L)
                .owner(user)
                .description("Test")
                .name("Test")
                .available(true).build();
        item = toItem(itemService.addItem(1L, toItemDto(item)));
        user1 = User.builder()
                .id(2L)
                .name("test1")
                .email("test1@test.ru").build();
        user1 = toUser(userService.addUser(toUserDto(user1)));
        item1 = Item.builder()
                .id(2L)
                .owner(user1)
                .description("Test1")
                .name("Test1")
                .available(true).build();
        item1 = toItem(itemService.addItem(2L, toItemDto(item1)));
        item2 = Item.builder()
                .id(3L)
                .owner(user)
                .description("Test2")
                .name("Test2")
                .available(false).build();
        item2 = toItem(itemService.addItem(1L, toItemDto(item2)));
        booking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(user1)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .status(Status.WAITING).build());
        booking1 = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(user1)
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .status(Status.WAITING).build());
        booking2 = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(user1)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .status(Status.WAITING).build());
    }

    @Test
    void addBooking() throws ValidationException {
        BookingEntryDto bookingEntryDto = BookingEntryDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now()).build();
        bookingService.addBooking(2L, bookingEntryDto);
        List<BookingDto> testBookings1 = bookingService.getAllBookingByState(2L, "WAITING", 0, 10);

        assertEquals(2L, testBookings1.get(3).getId());
        assertTrue(testBookings1.get(2).getStart().isAfter(testBookings1.get(3).getStart()));
        assertTrue(testBookings1.get(3).getStart().isBefore(testBookings1.get(1).getStart()));
    }

    @Test
    void addBooking_InvalidUserId() {
        BookingEntryDto bookingEntryDto = BookingEntryDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now()).build();

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(100L, bookingEntryDto));
    }

    @Test
    void addBooking_InvalidItemId() {
        BookingEntryDto bookingEntryDto = BookingEntryDto.builder()
                .itemId(100L)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now()).build();

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(user.getId(), bookingEntryDto));
    }

    @Test
    void addBooking_OwnItem() {
        BookingEntryDto bookingEntryDto = BookingEntryDto.builder()
                .itemId(1L).start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2)).build();

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(user.getId(), bookingEntryDto));
    }

    @Test
    void addBooking_Unavailable() {
        BookingEntryDto bookingEntryDto = BookingEntryDto.builder()
                .itemId(3L)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().minusHours(2)).build();

        assertThrows(NotAvailableException.class, () -> bookingService.addBooking(user.getId(), bookingEntryDto));
    }

    @Test
    void addBooking_InvalidDate() {
        BookingEntryDto bookingEntryDto = BookingEntryDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().minusHours(2)).build();

        assertThrows(NotAvailableException.class, () -> bookingService.addBooking(user.getId(), bookingEntryDto));
    }

    @Test
    void approveBooking() throws ValidationException {
        bookingService.approveBooking(1L, 1L, true);
        bookingService.approveBooking(1L, 3L, false);
        List<BookingDto> testBookings = bookingService.getAllBookingByState(2L, "WAITING", 0, 10);

        assertEquals(1, testBookings.size());

        List<BookingDto> testBookingStatusCurrent = bookingService.getAllBookingByState(2L, "CURRENT", 0, 10);

        assertEquals(1, testBookingStatusCurrent.size());
        assertEquals(1L, testBookingStatusCurrent.get(0).getId());
    }

    @Test
    void disApproveBooking() throws ValidationException {
        bookingService.approveBooking(1L, 3L, false);
        List<BookingDto> testBookingStatusRejected = bookingService.getAllBookingByState(2L, "REJECTED", 0, 10);

        assertEquals(1, testBookingStatusRejected.size());
        assertEquals(3L, testBookingStatusRejected.get(0).getId());
    }

    @Test
    void approveBooking_NotOwner() {
        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(2L, 1L, true));
    }

    @Test
    void approveBooking_InvalidUser() {
        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(100L, 1L, true));
    }

    @Test
    void approveBooking_IvalidBooking() {
        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(1L, 100L, true));
    }

    @Test
    void approveBooking_Duplicate() {
        bookingService.approveBooking(1L, 1L, true);
        assertThrows(NotAvailableException.class, () -> bookingService.approveBooking(1L, 1L, true));
    }

    @Test
    void getBookingById() {
        BookingDto bookingDto = bookingService.getBookingById(1L, 1L);

        assertEquals(1L, bookingDto.getId());
        assertEquals(1L, bookingDto.getItem().getId());
        assertEquals("test1", bookingDto.getBooker().getName());
        assertEquals("test", bookingDto.getItem().getOwner().getName());
        assertEquals(Status.WAITING, bookingDto.getStatus());
        assertTrue(LocalDateTime.now().isBefore(bookingDto.getEnd()));
        assertTrue(LocalDateTime.now().isAfter(bookingDto.getStart()));
    }

    @Test
    void getBookingById_InvalidUser() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(99L, 1L));
    }

    @Test
    void getBookingById_InvalidItem() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(1L, 111L));
    }

    @Test
    void getAllBookingByStateWaiting() throws ValidationException {
        bookingService.approveBooking(1L, 1L, true);
        bookingService.approveBooking(1L, 3L, false);
        List<BookingDto> testBookings = bookingService.getAllBookingByState(2L, "WAITING", 0, 10);

        assertEquals(1, testBookings.size());
    }

    @Test
    void getAllBooking_InvalidUser() {
        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingByState(99L, "WAITING", 0, 10));
    }

    @Test
    void getAllBooking_InvalidState() {
        assertThrows(NotSupportedStateException.class, () -> bookingService.getAllBookingByState(1L, "NOTSUPP", 0, 10));
    }

    @Test
    void getAllBookingByStateRejected() throws ValidationException {
        bookingService.approveBooking(1L, 1L, true);
        bookingService.approveBooking(1L, 3L, false);
        List<BookingDto> testBookingStatusRejected = bookingService.getAllBookingByState(2L, "REJECTED", 0, 10);

        assertEquals(1, testBookingStatusRejected.size());
        assertEquals(3L, testBookingStatusRejected.get(0).getId());
    }

    @Test
    void getAllBookingByStateCurrent() throws ValidationException {
        bookingService.approveBooking(1L, 1L, true);
        bookingService.approveBooking(1L, 3L, false);
        List<BookingDto> testBookingStatusCurrent = bookingService.getAllBookingByState(2L, "CURRENT", 0, 10);

        assertEquals(1, testBookingStatusCurrent.size());
        assertEquals(1L, testBookingStatusCurrent.get(0).getId());
    }

    @Test
    void getAllBookingByStateAll() throws ValidationException {
        bookingService.approveBooking(1L, 1L, true);
        bookingService.approveBooking(1L, 3L, false);
        List<BookingDto> testBookingStatusAll = bookingService.getAllBookingByState(2L, "ALL", 0, 10);

        assertEquals(3, testBookingStatusAll.size());
        assertEquals(3L, testBookingStatusAll.get(0).getId());
        assertEquals(1L, testBookingStatusAll.get(1).getId());
        assertEquals(2L, testBookingStatusAll.get(2).getId());
    }

    @Test
    void getAllBookingByStateAll_invalidUser() {
        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingByState(999L, "ALL", 0, 10));
    }

    @Test
    void getAllOwnerBookingByStateAll() throws ValidationException {
        bookingService.approveBooking(1L, 1L, true);
        bookingService.approveBooking(1L, 3L, false);
        List<BookingDto> testBookingStatusAll = bookingService.getAllOwnersBookingByState(1L, "ALL", 0, 10);

        assertEquals(3, testBookingStatusAll.size());
        assertEquals(3L, testBookingStatusAll.get(0).getId());
        assertEquals(1L, testBookingStatusAll.get(1).getId());
        assertEquals(2L, testBookingStatusAll.get(2).getId());
    }

    @Test
    void getAllOwnerBookingByStateAll_InvalidUser() {
        assertThrows(NotFoundException.class, () -> bookingService.getAllOwnersBookingByState(999L, "ALL", 0, 10));
    }

    @Test
    void getAllBookingByStatePast() throws ValidationException {
        bookingService.approveBooking(1L, 1L, true);
        bookingService.approveBooking(1L, 3L, false);
        List<BookingDto> testBookingStatusPast = bookingService.getAllBookingByState(2L, "PAST", 0, 10);

        assertEquals(1, testBookingStatusPast.size());
        assertEquals(2L, testBookingStatusPast.get(0).getId());
    }

    @Test
    void getAllBookingByStateFuture() throws ValidationException {
        bookingService.approveBooking(1L, 1L, true);
        bookingService.approveBooking(1L, 3L, false);
        List<BookingDto> testBookingStatusFuture = bookingService.getAllBookingByState(2L, "FUTURE", 0, 10);

        assertEquals(1, testBookingStatusFuture.size());
        assertEquals(3L, testBookingStatusFuture.get(0).getId());
    }

    @Test
    void getAllOwnersBookingByStateWaiting() throws ValidationException {
        bookingService.approveBooking(1L, 1L, true);
        bookingService.approveBooking(1L, 3L, false);
        List<BookingDto> testBookings = bookingService.getAllOwnersBookingByState(1L, "WAITING", 0, 10);

        assertEquals(1, testBookings.size());
        assertEquals(2L, testBookings.get(0).getId());
        assertEquals(1L, testBookings.get(0).getItem().getOwner().getId());
    }

    @Test
    void getAllOwnersBooking_InvalidUser() {
        assertThrows(NotFoundException.class, () -> bookingService.getAllOwnersBookingByState(99L, "WAITING", 0, 10));
    }

    @Test
    void getAllOwnersBooking_InvalidState() {
        assertThrows(NotSupportedStateException.class, () -> bookingService.getAllOwnersBookingByState(1L, "NOTSUPP", 0, 10));
    }

    @Test
    void getAllOwnersBookingByStateRejected() throws ValidationException {
        bookingService.approveBooking(1L, 1L, true);
        bookingService.approveBooking(1L, 3L, false);
        List<BookingDto> testBookingStatusRejected = bookingService.getAllOwnersBookingByState(1L, "REJECTED", 0, 10);

        assertEquals(1, testBookingStatusRejected.size());
        assertEquals(3L, testBookingStatusRejected.get(0).getId());
        assertEquals(1L, testBookingStatusRejected.get(0).getItem().getOwner().getId());
    }

    @Test
    void getAllOwnersBookingByStateCurrent() throws ValidationException {
        bookingService.approveBooking(1L, 1L, true);
        bookingService.approveBooking(1L, 3L, false);
        List<BookingDto> testBookingStatusCurrent = bookingService.getAllOwnersBookingByState(1L, "CURRENT", 0, 10);

        assertEquals(1, testBookingStatusCurrent.size());
        assertEquals(1L, testBookingStatusCurrent.get(0).getId());
        assertEquals(1L, testBookingStatusCurrent.get(0).getItem().getOwner().getId());
    }

    @Test
    void getAllOwnersBookingByStatePast() throws ValidationException {
        bookingService.approveBooking(1L, 1L, true);
        bookingService.approveBooking(1L, 3L, false);
        List<BookingDto> testBookingStatusPast = bookingService.getAllOwnersBookingByState(1L, "PAST", 0, 10);

        assertEquals(1, testBookingStatusPast.size());
        assertEquals(2L, testBookingStatusPast.get(0).getId());
        assertEquals(1L, testBookingStatusPast.get(0).getItem().getOwner().getId());
    }

    @Test
    void getAllOwnersBookingByStateFuture() throws ValidationException {
        bookingService.approveBooking(1L, 1L, true);
        bookingService.approveBooking(1L, 3L, false);
        List<BookingDto> testBookingStatusFuture = bookingService.getAllOwnersBookingByState(1L, "FUTURE", 0, 10);

        assertEquals(1, testBookingStatusFuture.size());
        assertEquals(3L, testBookingStatusFuture.get(0).getId());
        assertEquals(1L, testBookingStatusFuture.get(0).getItem().getOwner().getId());
    }
}
