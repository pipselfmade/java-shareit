package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.xml.bind.ValidationException;
import java.util.List;

public interface BookingService {
    BookingDto addBooking(Long id, BookingEntryDto bookingDto) throws ValidationException;

    BookingDto approveBooking(Long id, Long bookingId, Boolean approved);

    BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long id, @PathVariable Long bookingId);

    List<BookingDto> getAllBookingByState(Long id, String state, int from, int size) throws ValidationException;

    List<BookingDto> getAllOwnersBookingByState(Long id, String state, int from, int size) throws ValidationException;
}
