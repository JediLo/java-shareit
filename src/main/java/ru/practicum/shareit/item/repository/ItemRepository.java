package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;


public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findAllByOwnerId(Long userId);

    @Query("""
                    select i
                    from Item i
                    where i.available = true
                        and (lower(i.name) like lower(concat('%', :text, '%')))
                        or (lower(i.description) like lower(concat('%', :text, '%')))
            """)
    Collection<Item> searchAvailableItems(String text);
}