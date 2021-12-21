package org.devel.smarttracker.application.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class ItemDetailDto {

    private Long id;
    private Long itemId;
    private Double value;
    private String formattedValue;
    private LocalDateTime createdAt;

    public ItemDetailDto(Long id, Long itemId, Double value, LocalDateTime createdAt) {
        this.id = id;
        this.itemId = itemId;
        this.value = value;
        this.createdAt = createdAt;
    }
}
