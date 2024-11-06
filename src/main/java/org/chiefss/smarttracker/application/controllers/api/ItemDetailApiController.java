package org.chiefss.smarttracker.application.controllers.api;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.chiefss.smarttracker.application.exceptions.ObjectNotFoundException;
import org.chiefss.smarttracker.application.dto.ItemDetailDto;
import org.chiefss.smarttracker.application.services.ItemDetailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/item-detail")
public class ItemDetailApiController {

    private final ItemDetailService itemDetailService;

    @GetMapping("{itemId}")
    public List<ItemDetailDto> getItemDetailAllByItemId(@PathVariable Long itemId) {
        try {
            return itemDetailService.findAllItemDetailDtoByItemId(itemId);
        } catch (NotFoundException e) {
            throw new ObjectNotFoundException(e);
        }
    }
}
