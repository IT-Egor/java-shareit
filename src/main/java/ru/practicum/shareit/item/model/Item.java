package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
@Builder
// TODO исправить аннотации после добавления БД
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean isAvailable;
    private User owner;
}
