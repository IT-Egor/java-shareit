package ru.practicum.shareit.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requester;

    @NotNull
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

}