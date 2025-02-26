package ru.practicum.shareit.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {
}
