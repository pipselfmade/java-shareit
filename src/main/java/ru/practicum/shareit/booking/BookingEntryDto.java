package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.executable.ValidateOnExecution;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@ValidateOnExecution
public class BookingEntryDto {
    private Long id;

    @FutureOrPresent
    @NotNull
    private LocalDateTime start;

    @FutureOrPresent
    @NotNull
    private LocalDateTime end;

    @NotNull
    private Long itemId;
}
