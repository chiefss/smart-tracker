package org.chiefss.smarttracker.application.factories;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.chiefss.smarttracker.application.entities.Item;
import org.chiefss.smarttracker.application.entities.ItemDetail;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemDetailFactory {

    public static ItemDetail create(Item item, Double value) {
        return new ItemDetail(item, value);
    }
}
