package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "requests")
public class ItemRequest {
    @Id
    private Long id;

    @Column(name = "description")
    private String description;

    @JoinColumn(name = "requester")
    @ManyToOne(fetch = FetchType.LAZY)
    private User requester;

    @Column(name = "created")
    private LocalDateTime created;
}
