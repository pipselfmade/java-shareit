package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.BookingMapper.toBooking;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplIT {
    private final BookingService service;

    @MockBean
    private final BookingRepository bookingRepository;

    @MockBean
    private final ItemRepository itemRepository;

    @MockBean
    private final UserRepository userRepository;

    private final User user = User.builder()
            .id(2L).build();
    private final Item item = Item.builder()
            .id(1L)
            .available(true)
            .owner(user).build();
    private final BookingEntryDto bookingEntryDto = BookingEntryDto.builder()
            .id(1L)
            .itemId(item.getId())
            .start(LocalDateTime.now().plusSeconds(20))
            .end(LocalDateTime.now().plusMinutes(2)).build();
    private final BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .start(bookingEntryDto.getStart())
            .end(bookingEntryDto.getEnd())
            .item(item)
            .booker(new User())
            .status(Status.WAITING).build();

    @Test
    void createNewBooking_expectedCorrect_returnDto() throws ValidationException, javax.xml.bind.ValidationException {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(toBooking(bookingDto));

        BookingDto booking = service.addBooking(1L, bookingEntryDto);

        assertNotNull(booking);
        assertEquals(bookingDto.getId(), booking.getId());
        assertEquals(bookingDto.getStatus(), booking.getStatus());
    }

    @Test
    void createNewBooking_notAvailableUserId_throwsException() throws NotFoundException {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(toBooking(bookingDto));

        user.setId(1L);

        assertThrows(NotFoundException.class, () -> service.addBooking(1L, bookingEntryDto));
    }

    @Test
    void createNewBooking_itemNotAvailable_throwsException() throws NotAvailableException {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(toBooking(bookingDto));

        item.setAvailable(false);

        assertThrows(NotAvailableException.class, () -> service.addBooking(1L, bookingEntryDto));
    }

    @Test
    void setStatusBooking_incorrectUser() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.approveBooking(1L, 1L, true));
    }

    @Test
    void setStatusBooking_incorrectBooking() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        assertThrows(NotFoundException.class, () -> service.approveBooking(1L, 1L, true));
    }

    @Test
    void setStatusBooking_incorrectApprovedStatus() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(toBooking(bookingDto)));

        assertThrows(NotFoundException.class, () -> service.approveBooking(1L, 1L, true));
    }
}
