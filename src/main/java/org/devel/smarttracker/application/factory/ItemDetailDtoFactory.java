package org.devel.smarttracker.application.factory;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.devel.smarttracker.application.dto.ItemDetailDto;
import org.devel.smarttracker.application.entities.ItemDetail;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemDetailDtoFactory {
    
    public static ItemDetailDto create(ItemDetail itemDetail) {
        return new ItemDetailDto(
                itemDetail.getId(),
                itemDetail.getItem().getId(),
                itemDetail.getValue(),
                itemDetail.getCreatedAt());
    }

    public static ItemDetailDto create(Long itemId, Double value) {
        return new ItemDetailDto(null, itemId, value, null);
    }
}
