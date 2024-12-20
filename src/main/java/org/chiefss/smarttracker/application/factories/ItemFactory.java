package org.chiefss.smarttracker.application.factories;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.chiefss.smarttracker.application.entities.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemFactory {

    public static Item create(String name, String url, String selector, String breakSelector) {
        return new Item(name, url, selector, breakSelector);
    }
}
