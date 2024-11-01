package org.devel.smarttracker.application.mappers;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.devel.smarttracker.application.dto.ItemDetailDto;
import org.devel.smarttracker.application.entities.Item;
import org.devel.smarttracker.application.entities.ItemDetail;
import org.devel.smarttracker.application.factories.ItemDetailFactory;

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
