package org.devel.smarttracker.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.devel.smarttracker.application.entities.Item;
import org.devel.smarttracker.application.entities.ItemDetail;


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
