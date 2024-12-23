package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findAllByUserId(Long userId);

    @Query("select it " +
            "from Item as it " +
            "join it.user as u " +
            "where it.available = true " +
            "and (lower(it.name) like lower(concat('%', ?1, '%')) " +
            "or lower(it.description) like lower(concat('%', ?1, '%'))) ")
    Collection<Item> findAllByText(String text);

    Collection<ItemDtoShort> findAllByRequestId(Long requestId);
}
