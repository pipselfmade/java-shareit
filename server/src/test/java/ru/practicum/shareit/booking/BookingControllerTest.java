package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingEntity;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @MockBean
    BookingService bookingService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mvc;
    BookingDto bookingDto;
    BookingEntity bookingEntity;
    List<BookingDto> listDto;

    @BeforeEach
    public void setUp() {
        bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(1), null, null,
                BookingStatus.WAITING);
        bookingEntity = new BookingEntity(1L, LocalDateTime.now(), LocalDateTime.now(), 1L);
        listDto = new ArrayList<>(asList(bookingDto));
    }

    @Test
    void getBookingsOwner() throws Exception {
        when(bookingService.getBookingsOwner(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(listDto);
        mvc.perform(get("/bookings/owner").header("X-Sharer-User-Id", 1L)
                        .param("name", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingState() throws Exception {
        when(bookingService.getBookingState(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(listDto);
        mvc.perform(get("/bookings").header("X-Sharer-User-Id", 1L)
                        .param("name", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingDto);
        mvc.perform(get("/bookings/{id}", 1L).header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void createBooking() throws Exception {
        when(bookingService.createBooking(anyLong(), any())).thenReturn(bookingDto);
        mvc.perform(post("/bookings").header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingEntity))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void bookingStatus() throws Exception {
        when(bookingService.bookingStatus(anyLong(), anyLong(), any()))
                .thenReturn(bookingDto);
        mvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
    }
}