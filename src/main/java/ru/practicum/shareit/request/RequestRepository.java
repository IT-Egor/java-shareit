package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findRequestsByRequester_IdOrderByCreationDateDesc(Long requesterId);

    @Query("SELECT r FROM Request r ORDER BY r.creationDate DESC")
    List<Request> findAllOrderByCreationDateDesc();
}
