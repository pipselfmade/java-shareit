package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingEntity;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/owner")
    public List<BookingDto> getBookingsOwner(@RequestHeader("X-Sharer-User-Id") Long id,
                                             @RequestParam(defaultValue = "ALL") String state,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "5") Integer size) {
        log.info("GET bookings with owner(userId) and state: {}, {}", id, state);
        return bookingService.getBookingsOwner(id, state, from, size);
    }

    @GetMapping
    public List<BookingDto> getBookingState(@RequestHeader("X-Sharer-User-Id") Long id,
                                            @RequestParam(defaultValue = "ALL") String state,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "5") Integer size) {
        log.info("GET bookings with userId and state: {}, {}", id, state);
        return bookingService.getBookingState(id, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId) {
        log.info("GET booking with userId and bookingId: {}, {}", userId, bookingId);
        return bookingService.getBooking(userId, bookingId);
    }

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody BookingEntity bookingEntity) {
        log.info("POST booking with userId: {}", userId);
        return bookingService.createBooking(userId, bookingEntity);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto bookingStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long bookingId,
                                    @RequestParam Boolean approved) {
        log.info("PATCH booking with userId and bookingId: {}, {}", userId, bookingId);
        return bookingService.bookingStatus(userId, bookingId, approved);
    }
}
