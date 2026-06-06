package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;


public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findAllByOwnerId(Long userId, Pageable pageable);

    @Query("""
            select i
            from Item i
            where i.available = true
              and (
                    lower(i.name) like lower(concat('%', :text, '%'))
                    or lower(i.description) like lower(concat('%', :text, '%'))
                  )
            """)
    Page<Item> searchAvailableItems(String text, Pageable pageable);

    Collection<Item> findAllByRequestId(Long requestId);
}