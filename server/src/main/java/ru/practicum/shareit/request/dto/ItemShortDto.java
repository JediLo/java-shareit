package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ItemShortDto {
    private Long itemId;
    private String name;
    private Long ownerId;

    public ItemShortDto(Item item) {
        this.itemId = item.getId();
        this.name = item.getName();
        this.ownerId = item.getOwner().getId();
    }
}
