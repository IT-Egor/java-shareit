package ru.practicum.shareit.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
// TODO исправить аннотации после добавления БД
public class User {
    private Long id;
    private String name;
    private String email;
}