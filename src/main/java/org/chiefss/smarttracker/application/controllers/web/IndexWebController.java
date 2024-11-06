package org.chiefss.smarttracker.application.controllers.web;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.chiefss.smarttracker.application.dto.ItemCreateDto;
import org.chiefss.smarttracker.application.dto.ItemUpdateDto;
import org.chiefss.smarttracker.application.entities.Item;
import org.chiefss.smarttracker.application.exceptions.ObjectNotFoundException;
import org.chiefss.smarttracker.application.mappers.ItemCreateDtoMapper;
import org.chiefss.smarttracker.application.mappers.ItemUpdateDtoMapper;
import org.chiefss.smarttracker.application.services.ItemDetailParserService;
import org.chiefss.smarttracker.application.services.ItemDetailService;
import org.chiefss.smarttracker.application.services.ItemService;
import org.chiefss.smarttracker.application.utils.Defines;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
public class IndexWebController {

    private final ItemService itemService;
    private final ItemDetailService itemDetailService;
    private final ItemDetailParserService itemDetailParserService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("items", itemService.findItemViewDtoAll());
        return "index";
    }

    @GetMapping("/view/{itemId}")
    public String viewById(Model model, @PathVariable Long itemId) {
        try {
            model.addAttribute("item", itemService.findByIdItemView(itemId));
            model.addAttribute("itemDetails", itemDetailService.findAllItemDetailViewByItemId(itemId, Defines.ITEM_DETAIL_VALUE_LIMIT));
            model.addAttribute("items", itemService.findItemViewDtoAll());
        } catch (NotFoundException e) {
            throw new ObjectNotFoundException(e);
        }
        return "index";
    }

    @PostMapping("/create")
    public String createItem(@RequestParam String name, @RequestParam String url,
                             @RequestParam String selector, @RequestParam(value = "break_selector") String breakSelector,
                             @RequestParam(name = "parse_now", required = false) boolean parseNow) {
        ItemCreateDto itemCreateDto = ItemCreateDtoMapper.toDto(name, url, selector, breakSelector);
        Item createdItem = itemService.create(itemCreateDto);
        if (createdItem != null && parseNow) {
            itemDetailParserService.parse(createdItem);
        }
        return redirectTo(String.format("/view/%d", createdItem.getId()));
    }

    @PostMapping("/update")
    public String updateItem(@RequestParam Long id, @RequestParam String name, @RequestParam String url,
                             @RequestParam String selector, @RequestParam(value = "break_selector") String breakSelector,
                             @RequestParam(name = "parse_now", required = false) boolean parseNow) throws NotFoundException {
        ItemUpdateDto itemUpdateDto = ItemUpdateDtoMapper.toDto(id, name, url, selector, breakSelector);
        Item updatedItem = itemService.update(itemUpdateDto);
        if (parseNow) {
            itemDetailParserService.parse(updatedItem);
        }
        return redirectTo(String.format("/view/%d", itemUpdateDto.getId()));
    }

    @GetMapping("/parseAll")
    public String parseAll() {
        itemDetailParserService.parseAll();
        return redirectToRoot();
    }

    @PostMapping("/activate/{itemId}")
    public String activateItem(@PathVariable Long itemId) throws NotFoundException {
        itemService.activate(itemId);
        return redirectToRoot();
    }

    @PostMapping("/deactivate/{itemId}")
    public String deactivateItem(@PathVariable Long itemId) throws NotFoundException {
        itemService.deactivate(itemId);
        return redirectToRoot();
    }

    @GetMapping("/detail/clean/{itemId}")
    public String cleanDetailAllByItemId(@PathVariable Long itemId) {
        try {
            itemDetailService.cleanDetailDuplicates(itemId);
        } catch (NotFoundException e) {
            throw new ObjectNotFoundException(e);
        }
        return String.format("redirect:/view/%d", itemId);
    }

    @GetMapping("/detail/cleanall")
    public String cleanItemDetailAll() {
        List<Item> items = itemService.findAll();
        try {
            for (Item item : items) {
                itemDetailService.cleanDetailDuplicates(item.getId());
            }
        } catch (NotFoundException e) {
            throw new ObjectNotFoundException(e);
        }
        return redirectToRoot();
    }

    @PostMapping("/delete/{itemId}")
    public String deleteItem(@PathVariable Long itemId) throws NotFoundException {
        itemService.delete(itemId);
        return redirectToRoot();
    }

    @GetMapping("/favicon.ico")
    @ResponseBody
    public void returnNoFavicon() {

    }

    private String redirectToRoot() {
        return redirectTo("/");
    }

    private String redirectTo(String url) {
        return "redirect:" + url;
    }
}
