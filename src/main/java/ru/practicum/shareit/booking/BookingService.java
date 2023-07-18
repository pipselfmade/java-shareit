package ru.practicum.shareit.booking;

import javax.xml.bind.ValidationException;
import java.util.List;

public interface BookingService {
    BookingDto addBooking(Long id, BookingEntryDto bookingDto) throws ValidationException;

    BookingDto approveBooking(Long id, Long bookingId, Boolean approved);

    BookingDto getBookingById(Long id, Long bookingId);

    List<BookingDto> getAllBookingByState(Long id, String state);

    List<BookingDto> getAllOwnersBookingByState(Long id, String state);
}
