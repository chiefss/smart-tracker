package org.chiefss.smarttracker.application.dto.internal;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class ItemDetailViewDto {

    private Long id;
    private Double value;
    private String formattedValue;
    private LocalDateTime createdAt;

    public ItemDetailViewDto(Long id, Double value, LocalDateTime createdAt) {
        this.id = id;
        this.value = value;
        this.createdAt = createdAt;
    }
}
