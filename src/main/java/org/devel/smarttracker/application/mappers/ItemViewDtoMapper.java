package org.devel.smarttracker.application.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.devel.smarttracker.application.dto.internal.ItemViewDto;
import org.devel.smarttracker.application.entities.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemViewDtoMapper {

    public static ItemViewDto toDto(Item item) {
        return new ItemViewDto(
                item.getId(),
                item.getName(),
                item.getUrl(),
                item.getSelector(),
                item.getBreakSelector(),
                null,
                item.getCreatedAt(),
                item.getDeletedAt());
    }
}
