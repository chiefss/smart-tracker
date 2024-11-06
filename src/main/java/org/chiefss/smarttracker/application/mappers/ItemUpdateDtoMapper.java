package org.chiefss.smarttracker.application.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.chiefss.smarttracker.application.dto.ItemUpdateDto;
import org.chiefss.smarttracker.application.entities.Item;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemUpdateDtoMapper {

    public static Item toEntity(ItemUpdateDto itemUpdateDto, Item item) {
        item.setName(itemUpdateDto.getName());
        item.setUrl(itemUpdateDto.getUrl());
        item.setSelector(itemUpdateDto.getSelector());
        item.setBreakSelector(itemUpdateDto.getBreakSelector());
        item.setUpdatedAt(LocalDateTime.now());
        return item;
    }

    public static ItemUpdateDto toDto(Long id, String name, String url, String selector, String breakSelector) {
        return new ItemUpdateDto(id, name, url, selector, breakSelector);
    }
}
