package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findAllByBooker_IdOrderByStartDateDesc(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.startDate < CURRENT_TIMESTAMP AND b.endDate > CURRENT_TIMESTAMP ORDER BY b.startDate DESC")
    Collection<Booking> findCurrentByBooker_Id(Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.endDate < CURRENT_TIMESTAMP ORDER BY b.startDate DESC")
    Collection<Booking> findPastByBooker_Id(Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.startDate > CURRENT_TIMESTAMP ORDER BY b.startDate DESC")
    Collection<Booking> findFutureByBooker_Id(Long bookerId);

    Collection<Booking> findAllByBooker_IdAndStatus(Long bookerId, Status status);
}
