package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingItemDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ItemDto {
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    @NotNull
    private Boolean available;

    private User owner;

    private Long requestId;

    private BookingItemDto lastBooking;

    private BookingItemDto nextBooking;

    private List<CommentDto> comments;
}
