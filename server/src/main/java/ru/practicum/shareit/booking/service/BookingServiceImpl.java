package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingEntity;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.Service.ItemServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.Service.UserServiceImpl;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.mapper.BookingMapper.listToBookingDto;
import static ru.practicum.shareit.booking.mapper.BookingMapper.makeBookingDto;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserServiceImpl userService;
    private final ItemServiceImpl itemService;

    Long id = 0L;

    @Transactional
    @Override
    public BookingDto createBooking(Long userId, BookingEntity bookingEntity) {
        if (userId > userService.returnId()) {
            throw new NotFoundException("Данного юзера не существует (Booking.create)");
        }

        User user = userRepository.getById(userId);

        if (bookingEntity.getItemId() > itemService.returnId()) {
            throw new NotFoundException("Данной вещи не существует (Booking.create)");
        }

        Item item = itemRepository.getById(bookingEntity.getItemId());

        if (userId.equals(item.getOwner().getId())) {
            throw new NotFoundException("Пользователь является собственником вещи (Booking.create)");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("Данную вещь нельзя забронировать (Booking.create)");
        }
        if (bookingEntity.getStart() == null ||
                bookingEntity.getEnd() == null || bookingEntity.getEnd().isBefore(LocalDateTime.now().minusMinutes(5)) ||
                bookingEntity.getStart().isBefore(LocalDateTime.now().minusMinutes(5)) ||
                bookingEntity.getEnd().isBefore(bookingEntity.getStart()) ||
                bookingEntity.getEnd().equals(bookingEntity.getStart())) {
            throw new BadRequestException("Время окончания бронирования указано не верно (Booking.create)");
        }

        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setId(makeId());
        booking.setItem(item);
        booking.setStart(bookingEntity.getStart());
        booking.setEnd(bookingEntity.getEnd());
        booking.setStatus(BookingStatus.WAITING);
        BookingDto bookingDto = makeBookingDto(booking);
        bookingRepository.save(booking);
        return bookingDto;
    }

    @Transactional
    @Override
    public BookingDto bookingStatus(Long userId, Long bookingId, Boolean approve) {
        Booking booking = bookingRepository.getById(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Подтверждать статус имеет права только собственник вещи(Booking.status)");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BadRequestException("Статус уже подтвержден(Booking.status)");
        }
        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        bookingRepository.save(booking);
        return makeBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto getBooking(Long userId, Long bookerId) {
        Booking booking = bookingRepository.getById(bookerId);

        if (userId > userService.returnId()) {
            throw new NotFoundException("Данного юзера не существует (Booking.create)");
        }
        if (bookerId > returnId()) {
            throw new NotFoundException("Данная бронь отсутствует(Booking.get)");
        }
        if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new NotFoundException("Вы не являетесь собственником");
        }
        return makeBookingDto(booking);
    }

    @Transactional
    @Override
    public List<BookingDto> getBookingsOwner(Long userId, String state, Integer from, Integer size) {
        if (userId > userService.returnId()) {
            throw new NotFoundException("Данного юзера не существует (Booking.create)");
        }

        int page = from >= 0 ? Math.round((float) from / size) : -1;
        Pageable pageable = PageRequest.of(page, size).withSort(Sort.by("id").descending());

        BookingState bookingState = BookingState.valueOf(state);

        switch (bookingState) {
            case ALL:
                return listToBookingDto(bookingRepository.findAllByBookerOwnerIdOrderByDesc(userId, pageable));
            case CURRENT:
                return listToBookingDto(bookingRepository.findAllByBookerOwnerIdAndStartBeforeAndEndAfterOrderByDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now(), pageable));
            case PAST:
                return listToBookingDto(bookingRepository.findAllByBookerOwnerIdAndEndBeforeOrderByDesc(userId,
                        LocalDateTime.now(), pageable));
            case FUTURE:
                return listToBookingDto(bookingRepository.findAllByBookerOwnerIdAndBookerStartAfterOrderByDesc(userId,
                        LocalDateTime.now(), pageable));
            case WAITING:
                return listToBookingDto(bookingRepository.findAllByBookerOwnerIdAndBookerStatusWaitingOrderByDesc(
                        userId, BookingStatus.WAITING, pageable));
            case REJECTED:
                return listToBookingDto(bookingRepository.findAllByBookerOwnerIdAndBookerStatusRejectedOrderByDesc(
                        userId, BookingStatus.REJECTED, pageable));
            case UNSUPPORTED_STATUS:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return null;
    }

    @Transactional
    @Override
    public List<BookingDto> getBookingState(Long userId, String state, Integer from, Integer size) {
        if (userId > userService.returnId()) {
            throw new NotFoundException("Данного юзера не существует (Booking.create)");
        }

        int page = from >= 0 ? Math.round((float) from / size) : -1;
        Pageable pageable = PageRequest.of(page, size).withSort(Sort.by("id").descending());

        BookingState bookingState = BookingState.valueOf(state);

        switch (bookingState) {
            case ALL:
                return listToBookingDto(bookingRepository.findAllByBookerIdOrderByDesc(userId, pageable));
            case CURRENT:
                return listToBookingDto(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now(), pageable));
            case PAST:
                return listToBookingDto(bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByDesc(userId,
                        LocalDateTime.now(), pageable));
            case FUTURE:
                return listToBookingDto(bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), pageable));
            case WAITING:
                return listToBookingDto(bookingRepository.findAllByBookerIdAndBookerStatusWaitingOrderByDesc(userId,
                        BookingStatus.WAITING, pageable));
            case REJECTED:
                return listToBookingDto(bookingRepository.findAllByBookerIdAndBookerStatusRejectedOrderByDesc(userId,
                        BookingStatus.REJECTED, pageable));
            case UNSUPPORTED_STATUS:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return null;
    }

    @Override
    public Long returnId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    private Long makeId() {
        id += 1;
        return id;
    }
}
