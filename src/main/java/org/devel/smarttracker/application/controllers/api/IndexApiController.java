package org.devel.smarttracker.application.controllers.api;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.devel.smarttracker.application.dto.ItemDetailDto;
import org.devel.smarttracker.application.services.ItemDetailService;
import org.devel.smarttracker.application.utils.Defines;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = Defines.API_PREFIX)
@RequiredArgsConstructor
public class IndexApiController {

    private final ItemDetailService itemDetailService;

    @GetMapping("detail/{id}")
    public List<ItemDetailDto> getItemDetailAllByItemId(@PathVariable Long id) throws NotFoundException {
        return itemDetailService.findAllItemDetailDtoByItemId(id);
    }
}
