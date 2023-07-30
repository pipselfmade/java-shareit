package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentMapperTests {
    @Test
    void toComment() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Test comment")
                .created(LocalDateTime.now()).build();
        Comment comment = CommentMapper.toComment(commentDto);

        assertEquals(commentDto.getId(), comment.getId());
        assertEquals(commentDto.getText(), comment.getText());
        assertEquals(commentDto.getCreated(), comment.getCreated());
    }

    @Test
    void toCommentDto() {
        Comment comment = Comment.builder()
                .id(1L)
                .text("Test comment")
                .author(User.builder().id(1L).build())
                .item(Item.builder().id(1L).build())
                .created(LocalDateTime.now()).build();
        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
        assertEquals(comment.getItem().getId(), commentDto.getItemId());
        assertEquals(comment.getCreated(), commentDto.getCreated());
    }
}
