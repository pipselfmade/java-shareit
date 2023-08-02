package ru.practicum.shareitgateway.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long lastBookingId;

    private Long nextBookingId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long requestId;
}
