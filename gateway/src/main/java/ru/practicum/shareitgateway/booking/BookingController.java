package ru.practicum.shareitgateway.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.booking.dto.BookingDtoEntity;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@Slf4j
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private static final String HEADER = "X-Sharer-User-Id";

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOwner(@RequestHeader(HEADER) Long id,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(defaultValue = "5") Integer size) {
        log.info("New GET /bookings/owner request");
        return bookingClient.getBookingsOwner(id, state, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingState(@RequestHeader(HEADER) Long id,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "5") Integer size) {
        log.info("New GET /bookings request");
        return bookingClient.getBookingState(id, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(HEADER) Long userId,
                                             @PathVariable Long bookingId) {
        log.info("New GET /bookings/{} request", bookingId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(HEADER) Long userId,
                                                @Valid @RequestBody BookingDtoEntity bookingDtoEntity) {
        log.info("New POST /bookings request");
        return bookingClient.createBooking(userId, bookingDtoEntity);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> bookingStatus(@RequestHeader(HEADER) Long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam Boolean approved) {
        log.info("New PATCH /bookings/{} request", bookingId);
        return bookingClient.bookingStatus(userId, bookingId, approved);
    }
}
