package org.devel.smarttracker.application.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class ItemDetailDtoView {

    private Long id;
    private Double value;
    private String formattedValue;
    private LocalDateTime createdAt;

    public ItemDetailDtoView(Long id, Double value, LocalDateTime createdAt) {
        this.id = id;
        this.value = value;
        this.createdAt = createdAt;
    }
}
