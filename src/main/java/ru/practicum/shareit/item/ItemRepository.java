package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findAllByOwnerId(Long userId);

    @Query("select i from Item i where (lower(i.name) like lower(concat('%', ?1, '%')) " +
            "or lower(i.description) like lower(concat('%', ?1, '%'))) and i.available=true")
    Page<Item> findByNameOrDescriptionAvailable(String text, Pageable pageable);

    List<Item> findAllByRequestId(Long requestId);
}