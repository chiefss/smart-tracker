package org.devel.smarttracker.application.controllers.api;

import javassist.NotFoundException;
import org.devel.smarttracker.application.dto.ItemDetailDto;
import org.devel.smarttracker.application.entities.Item;
import org.devel.smarttracker.application.factory.ItemDetailDtoFactory;
import org.devel.smarttracker.application.services.ItemDetailService;
import org.devel.smarttracker.application.services.ItemService;
import org.devel.smarttracker.application.utils.CurrencyUtils;
import org.devel.smarttracker.application.utils.Defines;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = Defines.API_PREFIX)
public class IndexApiController {

    private final ItemService itemService;

    private final ItemDetailService itemDetailService;

    @Autowired
    public IndexApiController(ItemService itemService, ItemDetailService itemDetailService) {
        this.itemService = itemService;
        this.itemDetailService = itemDetailService;
    }

    @GetMapping("detail/{id}")
    public List<ItemDetailDto> getItemDetailAllByItemId(@PathVariable Long id) throws NotFoundException {
        Item item = itemService.find(id);
        List<ItemDetailDto> itemDetailDtos = itemDetailService.findAll(item).stream().map(ItemDetailDtoFactory::create).toList();
        itemDetailDtos.forEach(itemDetailDto -> itemDetailDto.setFormattedValue(CurrencyUtils.formatCurrency(itemDetailDto.getValue())));
        return itemDetailDtos;
    }
}
