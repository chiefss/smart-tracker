package org.devel.smarttracker.application.factory;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.devel.smarttracker.application.dto.ItemDetailDtoView;
import org.devel.smarttracker.application.entities.ItemDetail;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemDetailDtoViewFactory {

    public static ItemDetailDtoView create(ItemDetail itemDetail) {
        return new ItemDetailDtoView(
                itemDetail.getId(),
                itemDetail.getValue(),
                itemDetail.getCreatedAt());
    }
}
