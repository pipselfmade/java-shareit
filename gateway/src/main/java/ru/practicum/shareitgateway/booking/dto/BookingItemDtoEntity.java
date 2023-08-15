package ru.practicum.shareitgateway.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingItemDtoEntity {
    private Long id;
    private Long bookerId;
}
