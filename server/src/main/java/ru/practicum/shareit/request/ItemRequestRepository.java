package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("select r " +
            "from ItemRequest as r " +
            "where r.requester.id = :userId " +
            "order by r.created desc ")
    List<ItemRequest> getAllItemRequestForUser(Long userId);

    @Query("select r " +
            "from ItemRequest as r " +
            "where r.requester.id != :userId " +
            "order by r.created desc")
    List<ItemRequest> getAllItemRequestForUserNull(Long userId, Pageable pageable);
}
