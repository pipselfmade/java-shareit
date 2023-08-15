package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingEntity;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingEntity bookingEntity);

    BookingDto bookingStatus(Long userId, Long bookingId, Boolean approve);

    BookingDto getBooking(Long userId, Long id);

    List<BookingDto> getBookingsOwner(Long id, String state, Integer from, Integer size);

    List<BookingDto> getBookingState(Long id, String state, Integer from, Integer size);

    Long returnId();

    void setId(Long id);
}
