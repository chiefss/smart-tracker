package org.chiefss.smarttracker.application.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.chiefss.smarttracker.application.dto.ItemCreateDto;
import org.chiefss.smarttracker.application.entities.Item;
import org.chiefss.smarttracker.application.factories.ItemFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemCreateDtoMapper {

    public static Item toEntity(ItemCreateDto itemCreateDto) {
        return ItemFactory.create(
                itemCreateDto.getName(),
                itemCreateDto.getUrl(),
                itemCreateDto.getSelector(),
                itemCreateDto.getBreakSelector());
    }

    public static ItemCreateDto toDto(String name, String url, String selector, String breakSelector) {
        return new ItemCreateDto(name, url, selector, breakSelector);
    }
}
