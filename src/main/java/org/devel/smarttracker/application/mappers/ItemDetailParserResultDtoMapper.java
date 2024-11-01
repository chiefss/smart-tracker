package org.devel.smarttracker.application.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.devel.smarttracker.application.dto.ItemDetailParserResultDto;
import org.devel.smarttracker.application.entities.Item;
import org.devel.smarttracker.application.entities.ItemDetail;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemDetailParserResultDtoMapper {

    public static ItemDetailParserResultDto toDto(Item item, ItemDetail itemDetail, boolean success, String message) {
        return new ItemDetailParserResultDto(item, itemDetail, success, message);
    }
}
