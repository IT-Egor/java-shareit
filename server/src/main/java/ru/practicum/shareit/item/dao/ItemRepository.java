package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findItemsByOwnerId(Long ownerId);

    @Query("SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :query, '%')) AND i.available = true")
    List<Item> findItemsByNameLikeIgnoreCaseAndAvailableTrue(String query);

    List<Item> findItemsByRequest_IdIn(List<Long> requestIds);
}
