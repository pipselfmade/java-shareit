package ru.practicum.shareit.item.comment;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {
    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void makeComment() {
        CommentDto commentDto = easyRandom.nextObject(CommentDto.class);
        Comment comment = CommentMapper.makeComment(commentDto);
        assertEquals(comment.getId(), commentDto.getId());
    }

    @Test
    void makeCommentDto() {
        Comment comment = easyRandom.nextObject(Comment.class);
        CommentDto commentDto = CommentMapper.makeCommentDto(comment);
        assertEquals(comment.getId(), commentDto.getId());
    }

    @Test
    void makeCommentDtoList() {
        Comment comment = easyRandom.nextObject(Comment.class);
        List<Comment> list = new ArrayList<>();
        list.add(comment);
        List<CommentDto> listDto = CommentMapper.makeCommentDtoList(list);
        assertEquals(list.size(), listDto.size());
    }
}