package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

@Data
@Builder
// TODO исправить аннотации после добавления БД
public class ItemRequest {
    private Long id;
    private String description;
    private User requester;
    private LocalDate creationDate;
}
