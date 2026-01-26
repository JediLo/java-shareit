package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;


public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findAllByOwnerId(Long userId);

    Collection<Item> findByAvailableTrueAndNameContainingIgnoreCaseOrAvailableTrueAndDescriptionContainingIgnoreCase(String name,
                                                                                                  String description);
}