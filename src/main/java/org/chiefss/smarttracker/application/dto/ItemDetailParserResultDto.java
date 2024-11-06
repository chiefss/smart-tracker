package org.chiefss.smarttracker.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.chiefss.smarttracker.application.entities.Item;
import org.chiefss.smarttracker.application.entities.ItemDetail;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ItemDetailParserResultDto {

    private Item item;
    private ItemDetail itemDetail;
    private boolean success;
    private String message;
}
