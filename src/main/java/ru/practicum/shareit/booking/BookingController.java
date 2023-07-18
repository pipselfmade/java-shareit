package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;


import javax.validation.Valid;
import javax.xml.bind.ValidationException;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping()
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long id,
                                 @Valid @RequestBody BookingEntryDto bookingDto) throws ValidationException {
        log.info("Добавлен новый запрос: {}", bookingDto);
        return bookingService.addBooking(id, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long id,
                                     @PathVariable Long bookingId,
                                     @RequestParam Boolean approved) {
        log.info("Отправлен запрос на изменение статуса бронирования от владельца c id: {}", id);
        return bookingService.approveBooking(id, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long id,
                                     @PathVariable Long bookingId) {
        return bookingService.getBookingById(id, bookingId);
    }

    @GetMapping()
    public List<BookingDto> getAllBookingByState(@RequestHeader("X-Sharer-User-Id") Long id,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingByState(id, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllItemsBookings(@RequestHeader("X-Sharer-User-Id") Long id,
                                                @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllOwnersBookingByState(id, state);
    }
}
