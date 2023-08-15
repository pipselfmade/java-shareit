package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingItemEntity;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingItemEntity lastBooking;

    private BookingItemEntity nextBooking;

    private List<CommentDto> comments;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long requestId;
}
