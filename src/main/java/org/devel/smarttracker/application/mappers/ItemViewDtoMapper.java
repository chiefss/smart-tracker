package org.devel.smarttracker.application.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.devel.smarttracker.application.dto.internal.ItemViewDto;
import org.devel.smarttracker.application.entities.Item;
import org.devel.smarttracker.application.entities.ItemDetail;
import org.devel.smarttracker.application.utils.ItemViewUtils;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemViewDtoMapper {

    public static ItemViewDto toDto(Item item, List<ItemDetail> itemDetails) {
        ItemViewDto itemViewDto = new ItemViewDto(
                item.getId(),
                item.getName(),
                item.getUrl(),
                item.getSelector(),
                item.getBreakSelector(),
                new ArrayList<>(),
                item.getCreatedAt(),
                item.getDeletedAt());
        ItemViewUtils.updateFormattedLastValue(itemViewDto, itemDetails);
        ItemViewUtils.updateDelta(itemViewDto, itemDetails);
        ItemViewUtils.updateHost(itemViewDto);
        return itemViewDto;
    }
}
