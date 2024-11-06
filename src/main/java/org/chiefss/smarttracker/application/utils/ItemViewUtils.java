package org.chiefss.smarttracker.application.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.chiefss.smarttracker.application.dto.internal.ItemViewDto;
import org.chiefss.smarttracker.application.entities.ItemDetail;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemViewUtils {

    public static void updateFormattedLastValue(ItemViewDto itemViewDto, List<ItemDetail> itemDetails) {
        double lastValue = itemDetails.isEmpty() ? 0.0 : itemDetails.get(0).getValue();
        String formattedLastValue = CurrencyUtils.formatCurrency(lastValue);
        itemViewDto.setFormattedLastValue(formattedLastValue);
    }

    public static void updateDelta(ItemViewDto itemViewDto, List<ItemDetail> itemDetails) {
        double delta = 0.0;
        int itemValueCount = itemDetails.size();
        if (itemValueCount == 2) {
            Double lastValue = itemDetails.get(0).getValue();
            Double prevValue = itemDetails.get(1).getValue();
            if (!lastValue.equals(prevValue)) {
                delta = lastValue - prevValue;
            }
        }
        itemViewDto.setDelta(delta);
        itemViewDto.setFormattedDelta(CurrencyUtils.formatCurrency(Math.abs(delta)));
    }

    public static void updateHost(ItemViewDto itemViewDto) {
        try {
            URL url = new URL(itemViewDto.getUrl());
            itemViewDto.setHost(url.getHost());
        } catch (MalformedURLException e) {
            log.warn("Cannot parse host from Item url: \"{}\"", itemViewDto.getUrl());
        }
    }
}
