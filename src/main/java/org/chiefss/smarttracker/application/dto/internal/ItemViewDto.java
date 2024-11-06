package org.chiefss.smarttracker.application.dto.internal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ItemViewDto {

    private Long id;
    private String name;
    private String url;
    private String host;
    private String selector;
    private String breakSelector;
    private List<ItemDetailViewDto> values = new ArrayList<>();
    private String formattedLastValue;
    private Double delta;
    private String formattedDelta;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public ItemViewDto(Long id, String name, String url, String selector, String breakSelector,
                       List<ItemDetailViewDto> values,
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
