package org.devel.smarttracker.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ItemDtoView {

    private Long id;
    private String name;
    private String url;
    private String host;
    private String selector;
    private String breakSelector;
    private List<ItemDetailDtoView> values = new ArrayList<>();
    private String formattedLastValue;
    private Double delta;
    private String formattedDelta;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public ItemDtoView(Long id, String name, String url, String selector, String breakSelector,
                       List<ItemDetailDtoView> values,
                       LocalDateTime createdAt, LocalDateTime deletedAt) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.selector = selector;
        this.breakSelector = breakSelector;
        this.values = values;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public boolean isDeltaPlus() {
        return delta > 0;
    }

    public boolean isDeltaMinus() {
        return delta < 0;
    }
}
