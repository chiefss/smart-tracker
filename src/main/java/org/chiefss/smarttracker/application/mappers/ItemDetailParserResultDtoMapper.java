package org.chiefss.smarttracker.application.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.chiefss.smarttracker.application.dto.ItemDetailParserResultDto;
import org.chiefss.smarttracker.application.entities.Item;
import org.chiefss.smarttracker.application.entities.ItemDetail;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemDetailParserResultDtoMapper {

    public static ItemDetailParserResultDto toDto(Item item, ItemDetail itemDetail, boolean success, String message) {
        return new ItemDetailParserResultDto(item, itemDetail, success, message);
    }
}
