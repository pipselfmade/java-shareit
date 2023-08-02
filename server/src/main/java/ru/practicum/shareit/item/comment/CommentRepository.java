package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(" select c from Comment as c" +
            " where c.item.id = ?1" +
            " order by c.created desc")
    List<Comment> getCommentsForItem(Long itemId);
}
