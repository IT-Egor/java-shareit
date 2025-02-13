package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

@Data
@Builder
// TODO исправить аннотации после добавления БД
public class Booking {
    private Long id;
    private Item item;
    private User booker;
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;
}
