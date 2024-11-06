package org.chiefss.smarttracker.application.mappers;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.chiefss.smarttracker.application.dto.internal.ItemDetailViewDto;
import org.chiefss.smarttracker.application.entities.ItemDetail;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemDetailViewDtoMapper {

    public static ItemDetailViewDto toDto(ItemDetail itemDetail) {
        return new ItemDetailViewDto(
                itemDetail.getId(),
                itemDetail.getValue(),
                itemDetail.getCreatedAt());
    }
}
