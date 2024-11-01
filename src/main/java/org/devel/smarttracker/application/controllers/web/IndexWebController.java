package org.devel.smarttracker.application.controllers.web;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.devel.smarttracker.application.dto.ItemCreateDto;
import org.devel.smarttracker.application.dto.ItemUpdateDto;
import org.devel.smarttracker.application.entities.Item;
import org.devel.smarttracker.application.mappers.ItemCreateDtoMapper;
import org.devel.smarttracker.application.mappers.ItemUpdateDtoMapper;
import org.devel.smarttracker.application.services.ItemDetailParserService;
import org.devel.smarttracker.application.services.ItemDetailService;
import org.devel.smarttracker.application.services.ItemService;
import org.devel.smarttracker.application.utils.Defines;
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

    @GetMapping("/view/{id}")
    public String viewById(Model model, @PathVariable Long id) throws NotFoundException {
        model.addAttribute("item", itemService.findByIdItemView(id));
        model.addAttribute("itemDetails", itemDetailService.findAllItemDetailViewByItemId(id, Defines.ITEM_DETAIL_VALUE_LIMIT));
        model.addAttribute("items", itemService.findItemViewDtoAll());
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

    @PostMapping("/activate/{id}")
    public String activateItem(@PathVariable Long id) throws NotFoundException {
        itemService.activate(id);
        return redirectToRoot();
    }

    @PostMapping("/deactivate/{id}")
    public String deactivateItem(@PathVariable Long id) throws NotFoundException {
        itemService.deactivate(id);
        return redirectToRoot();
    }

    @GetMapping("/detail/clean/{id}")
    public String cleanDetailAllByItemId(@PathVariable Long id) {
        itemDetailService.cleanDetailDuplicates(id);
        return String.format("redirect:/view/%d", id);
    }

    @GetMapping("/detail/cleanall")
    public String cleanItemDetailAll() {
        List<Item> items = itemService.findAll();
        for (Item item : items) {
            itemDetailService.cleanDetailDuplicates(item.getId());
        }
        return redirectToRoot();
    }

    @PostMapping("/delete/{id}")
    public String deleteItem(@PathVariable Long id) throws NotFoundException {
        itemService.delete(id);
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
