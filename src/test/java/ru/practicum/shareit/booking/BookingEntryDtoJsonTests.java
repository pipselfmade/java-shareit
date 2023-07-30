package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class BookingEntryDtoJsonTests {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .setDateFormat(new StdDateFormat().withColonInTimeZone(true));

    @Test
    public void serializeToJson() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        BookingEntryDto bookingEntryDto = BookingEntryDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 5, 1, 10, 0))
                .end(LocalDateTime.of(2023, 5, 1, 12, 0))
                .itemId(2L)
                .build();
        String json = objectMapper.writeValueAsString(bookingEntryDto);
        String expectedJson = "{\"id\":1,\"start\":\"2023-05-01T10:00:00\",\"end\":\"2023-05-01T12:00:00\",\"itemId\":2}";

        assertEquals(expectedJson, json);
    }

    @Test
    public void deserializeFromJson() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        String json = "{\"id\":1,\"start\":\"2023-05-01T10:00:00\",\"end\":\"2023-05-01T12:00:00\",\"itemId\":2}";
        BookingEntryDto bookingEntryDto = objectMapper.readValue(json, BookingEntryDto.class);
        BookingEntryDto expectedBookingEntryDto = BookingEntryDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 5, 1, 10, 0))
                .end(LocalDateTime.of(2023, 5, 1, 12, 0))
                .itemId(2L).build();

        assertEquals(expectedBookingEntryDto, bookingEntryDto);
    }
}
