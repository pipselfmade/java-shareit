package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Data
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "items")
public class Item {
    @Id
    private Long id;

    @JoinColumn(name = "owner")
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "available")
    private Boolean available;

    @JoinColumn(name = "request")
    @ManyToOne(fetch = FetchType.LAZY)
    private ItemRequest requestId;
}
