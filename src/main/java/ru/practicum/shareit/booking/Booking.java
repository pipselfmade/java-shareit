package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings", schema = "public")
public class Booking {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "start_date")
    private LocalDateTime start;

    @Column(name = "end_date")
    private LocalDateTime end;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "itemId")
    private Item item;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "bookerId")
    private User booker;

    @Enumerated(EnumType.STRING)
    @Column
    private Status status;
}
