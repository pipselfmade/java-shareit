package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotSupportedStateException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingMapper.toBookingDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDto addBooking(Long id, BookingEntryDto bookingDto) {
        validateDate(bookingDto);
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("item not found"));

        if (id.equals(item.getOwner().getId())) {
            throw new NotFoundException("You cant book your own item");
        }
        if (!item.getAvailable()) {
            throw new NotAvailableException("Not available");
        }

        Booking booking = Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd()).build();

        booking.setBooker(userRepository.findById(id).orElseThrow(() -> new NotFoundException("user not found")));
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        return toBookingDto(bookingRepository.save(booking));
    }

    private void validateDate(BookingEntryDto bookingDto) {
        if (bookingDto.getStart().isEqual(bookingDto.getEnd()) || bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new NotAvailableException("Incorrect date!");
        }
    }

    @Transactional
    @Override
    public BookingDto approveBooking(Long id, Long bookingId, Boolean approved) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found");
        }

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("No such booking"));

        if (booking.getItem().getOwner().getId().longValue() != id.longValue()) {
            throw new NotFoundException("User not the owner!");
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new NotAvailableException("Already approved!");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
            Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow();
            itemRepository.save(item);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        return toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDto getBookingById(Long id, Long bookingId) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found");
        }

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("No such booking"));

        if (booking.getItem().getOwner().getId() != id.longValue() && booking.getBooker().getId() != id.longValue()) {
            throw new NotFoundException("User not the owner or booker!");
        }

        return toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingByState(Long id, String state, int from, int size) throws ValidationException {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found!");
        }
        if (from < 0 || size <= 0) {
            throw new ValidationException("Not valid page");
        }

        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size,
                Sort.by(Sort.Direction.DESC, "start"));
        Page<Booking> bookingList;
        LocalDateTime now = LocalDateTime.now();

        switch (convert(state)) {
            case ALL:
                bookingList = bookingRepository.findAllByBookerId(id, pageable);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(id, now, now, pageable);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByBookerIdAndEndBefore(id, now, pageable);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByBookerIdAndStartAfter(id, now, pageable);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByBookerIdAndStatus(id, Status.WAITING, pageable);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByBookerIdAndStatus(id, Status.REJECTED, pageable);
                break;
            default:
                throw new NotSupportedStateException("Unknown state: " + state);
        }

        return bookingList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    public List<BookingDto> getAllOwnersBookingByState(Long id, String state, int from, int size) throws ValidationException {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found!");
        }
        if (from < 0 || size < 0) {
            throw new ValidationException("Not valid page");
        }

        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size,
                Sort.by(Sort.Direction.DESC, "start"));
        Page<Booking> bookingList;
        LocalDateTime now = LocalDateTime.now();

        switch (convert(state)) {
            case ALL:
                bookingList = bookingRepository.findAllByItemOwnerId(id, pageable);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(id, now, now, pageable);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByItemOwnerIdAndEndBefore(id, now, pageable);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStartAfter(id, now, pageable);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(id, Status.WAITING, pageable);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(id, Status.REJECTED, pageable);
                break;
            default:
                throw new NotSupportedStateException("Unknown state: " + state);
        }

        return bookingList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    private static State convert(String state) {
        try {
            return State.valueOf(state);
        } catch (Exception e) {
            throw new NotSupportedStateException("Unknown state: " + state);
        }
    }
}