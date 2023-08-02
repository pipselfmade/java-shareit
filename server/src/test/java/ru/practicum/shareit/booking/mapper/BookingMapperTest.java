package ru.practicum.shareit.booking.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemEntity;
import ru.practicum.shareit.booking.model.Booking;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {
    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void makeBooking() {
        BookingDto bookingDto = easyRandom.nextObject(BookingDto.class);
        Booking booking = BookingMapper.makeBooking(bookingDto);
        assertEquals(bookingDto.getStart(), booking.getStart());
    }

    @Test
    void makeBookingDto() {
        Booking booking = easyRandom.nextObject(Booking.class);
        BookingDto bookingDto = BookingMapper.makeBookingDto(booking);
        assertEquals(bookingDto.getStart(), booking.getStart());
    }

    @Test
    void makeBookingItemEntity() {
        Booking booking = easyRandom.nextObject(Booking.class);
        BookingItemEntity bookingEntity = BookingMapper.makeBookingItemEntity(booking);
        assertEquals(booking.getId(), bookingEntity.getId());
    }

    @Test
    void listToBookingDto() {
        Booking booking = easyRandom.nextObject(Booking.class);
        List<Booking> list = new ArrayList();
        list.add(booking);
        List<BookingDto> listDto = BookingMapper.listToBookingDto(list);
        assertEquals(list.size(), listDto.size());
    }
}