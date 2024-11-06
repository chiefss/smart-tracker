package org.chiefss.smarttracker.application.mappers;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.chiefss.smarttracker.application.entities.Item;
import org.chiefss.smarttracker.application.dto.ItemDetailDto;
import org.chiefss.smarttracker.application.entities.ItemDetail;
import org.chiefss.smarttracker.application.factories.ItemDetailFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemDetailDtoMapper {

    public static ItemDetail toEntity(ItemDetailDto itemDetailDto, Item item) {
        return ItemDetailFactory.create(item, itemDetailDto.getValue());
    }

    public static ItemDetailDto toDto(Long itemId, Double value) {
        return new ItemDetailDto(null, itemId, value, null);
    }

    public static ItemDetailDto toDto(ItemDetail itemDetail) {
        return new ItemDetailDto(
                itemDetail.getId(),
                itemDetail.getItem().getId(),
                itemDetail.getValue(),
                itemDetail.getCreatedAt());
    }
}
