package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemEntity;
import ru.practicum.shareit.booking.model.Booking;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.item.mapper.ItemMapper.makeItemDto;

@UtilityClass
public class BookingMapper {
    public static Booking makeBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        return booking;
    }

    public static BookingDto makeBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart((booking.getStart()));
        bookingDto.setEnd((booking.getEnd()));
        bookingDto.setItem(makeItemDto(booking.getItem()));
        bookingDto.setBooker(booking.getBooker());
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public static BookingItemEntity makeBookingItemEntity(Booking booking) {
        BookingItemEntity bookingItemEntity = new BookingItemEntity();
        bookingItemEntity.setId(booking.getId());
        bookingItemEntity.setBookerId(booking.getBooker().getId());
        return bookingItemEntity;
    }

    public static List<BookingDto> listToBookingDto(List<Booking> bookings) {
        List<BookingDto> dtos = new ArrayList<>();

        for (Booking booking : bookings) {
            dtos.add(makeBookingDto(booking));
        }
        return dtos;
    }
}
