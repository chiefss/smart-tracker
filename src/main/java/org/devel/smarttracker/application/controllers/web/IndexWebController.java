package org.devel.smarttracker.application.controllers.web;

import javassist.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.devel.smarttracker.application.dto.*;
import org.devel.smarttracker.application.entities.Item;
import org.devel.smarttracker.application.entities.ItemDetail;
import org.devel.smarttracker.application.factory.ItemDtoViewFactory;
import org.devel.smarttracker.application.factory.ItemDetailDtoFactory;
import org.devel.smarttracker.application.factory.ItemDetailDtoViewFactory;
import org.devel.smarttracker.application.parsers.ItemDetailParser;
import org.devel.smarttracker.application.services.ItemDetailService;
import org.devel.smarttracker.application.services.ItemService;
import org.devel.smarttracker.application.utils.Defines;
import org.devel.smarttracker.application.utils.CurrencyUtils;
import org.jsoup.HttpStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Controller
public class IndexWebController {

    private final ItemService itemService;

    private final ItemDetailService itemDetailService;

    private final ItemDetailParser itemDetailParser;

    @Autowired
    public IndexWebController(ItemService itemService, ItemDetailService itemDetailService, ItemDetailParser itemDetailParser) {
        this.itemService = itemService;
        this.itemDetailService = itemDetailService;
        this.itemDetailParser = itemDetailParser;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<ItemDtoView> itemDtoViews = getItemDtos();
        model.addAttribute("items", itemDtoViews);
        return "index";
    }

    @GetMapping("/view/{id}")
    public String viewById(Model model, @PathVariable Long id) throws NotFoundException {
        Item item = itemService.find(id);
        ItemDtoView itemDtoView = getItemDto(item);
        List<ItemDetailDtoView> itemDetailDtoViews = itemDetailService.findAll(item)
                .stream()
                .limit(Defines.ITEM_DETAIL_VALUE_LIMIT)
                .map(ItemDetailDtoViewFactory::create)
                .toList();
        itemDetailDtoViews.forEach(itemDetailDtoView ->
                itemDetailDtoView.setFormattedValue(CurrencyUtils.formatCurrency(itemDetailDtoView.getValue())));
        List<ItemDtoView> itemDtoViews = getItemDtos();
        model.addAttribute("item", itemDtoView);
        model.addAttribute("itemDetails", itemDetailDtoViews);
        model.addAttribute("items", itemDtoViews);
        return "index";
    }

    @PostMapping("/create")
    public String createItem(@RequestParam String name, @RequestParam String url,
                             @RequestParam String selector, @RequestParam(value = "break_selector") String breakSelector,
                             @RequestParam(name = "parse_now", required = false) boolean parseNow) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setUrl(url);
        itemDto.setSelector(selector);
        itemDto.setBreakSelector(breakSelector);
        Item createdItem = itemService.create(itemDto);
        if (createdItem != null && parseNow) {
            try {
                createItemDetail(createdItem);
            } catch (NotFoundException | IOException e) {
                log.error(String.format("Cannot parse item detail from url %s: %s", createdItem.getUrl(), e.getMessage()));
            }
        }
        return redirectTo(String.format("/view/%d", createdItem.getId()));
    }

    @PostMapping("/update")
    public String updateItem(@RequestParam Long id, @RequestParam String name, @RequestParam String url,
                             @RequestParam String selector, @RequestParam(value = "break_selector") String breakSelector,
                             @RequestParam(name = "parse_now", required = false) boolean parseNow) throws NotFoundException {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(id);
        itemDto.setName(name);
        itemDto.setUrl(url);
        itemDto.setSelector(selector);
        itemDto.setBreakSelector(breakSelector);
        Item updatedItem = itemService.update(itemDto);
        if (parseNow) {
            try {
                createItemDetail(updatedItem);
            } catch(HttpStatusException e) {
                log.error(e.getMessage());
            } catch (NotFoundException | IOException e) {
                log.error(String.format("Cannot parse item detail from url %s: %s", updatedItem.getUrl(), e.getMessage()));
            }
        }
        return redirectTo(String.format("/view/%d", itemDto.getId()));
    }

    @PostMapping("/delete/{id}")
    public String deleteItem(@PathVariable Long id) throws NotFoundException {
        itemService.delete(id);
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
    public String cleanDetailAllByItemId(@PathVariable Long id) throws NotFoundException {
        Item item = itemService.find(id);
        cleanDetailDuplicates(item);
        return String.format("redirect:/view/%d", item.getId());
    }

    @GetMapping("/detail/cleanall")
    public String cleanItemDetailAll() {
        List<Item> items = itemService.findAll(false);
        for (Item item : items) {
            cleanDetailDuplicates(item);
        }
        return redirectToRoot();
    }

    @GetMapping("/parseAll")
    public String parseAll() {
        List<ItemDetailParserResultDto> itemDetailParserResultDtos = itemDetailParser.parseAll();
        for (ItemDetailParserResultDto itemDetailParserResultDto : itemDetailParserResultDtos) {
            if (itemDetailParserResultDto.isSuccess()) {
                ItemDetail itemDetail = itemDetailParserResultDto.getItemDetail();
                ItemDetailDto itemDetailDto = ItemDetailDtoFactory.create(itemDetail.getItem().getId(), itemDetail.getValue());
                try {
                    itemDetailService.create(itemDetailDto);
                } catch (NotFoundException e) {
                    log.error(String.format("Cannot parse all and save item detail for item id \"%d\"", itemDetail.getItem().getId()));
                }
            }
        }
        return redirectToRoot();
    }

    @GetMapping("/favicon.ico")
    @ResponseBody
    public void returnNoFavicon() {

    }

    private void createItemDetail(Item createdItem) throws IOException, NotFoundException {
        Optional<ItemDetail> itemDetailOptional = itemDetailParser.parse(createdItem);
        if (itemDetailOptional.isPresent()) {
            ItemDetail itemDetail = itemDetailOptional.get();
            ItemDetailDto itemDetailDto = ItemDetailDtoFactory.create(itemDetail.getItem().getId(), itemDetail.getValue());
            itemDetailService.create(itemDetailDto);
        }
    }

    private void cleanDetailDuplicates(Item item) {
        List<ItemDetail> itemDetails = itemDetailService.findAll(item);
        Double prevValue = null;
        for (ItemDetail itemDetail : itemDetails) {
            Double currentValue = itemDetail.getValue();
            if (currentValue.equals(prevValue)) {
                itemDetailService.delete(itemDetail.getId());
            }
            prevValue = currentValue;
        }
    }

    private List<ItemDtoView> getItemDtos() {
        List<Item> items = itemService.findAll(false);
        List<ItemDtoView> itemDtoViews = new ArrayList<>();
        for (Item item : items) {
            ItemDtoView itemDtoView = getItemDto(item);
            itemDtoViews.add(itemDtoView);
        }
        return itemDtoViews;
    }

    private ItemDtoView getItemDto(Item item) {
        ItemDtoView itemDtoView = ItemDtoViewFactory.create(item);
        List<ItemDetail> itemDetails = itemDetailService.findLast(item);

        String formattedLastValue = CurrencyUtils.formatCurrency(getLastValue(itemDetails));
        itemDtoView.setFormattedLastValue(formattedLastValue);

        Double delta = getDelta(itemDetails);
        itemDtoView.setDelta(delta);
        itemDtoView.setFormattedDelta(CurrencyUtils.formatCurrency(Math.abs(delta)));

        try {
            URL url = new URL(item.getUrl());
            itemDtoView.setHost(url.getHost());
        } catch (MalformedURLException e) {
            log.debug(String.format("Cannot parse host by item entity with url \"%s\"", item.getUrl()));
        }

        return itemDtoView;
    }

    private Double getDelta(List<ItemDetail> itemDetails) {
        double lastDetailValue = 0.0;
        int itemValueCount = itemDetails.size();
        if (itemValueCount == 2) {
            Double todayValue = itemDetails.get(0).getValue();
            Double yesterdayValue = itemDetails.get(1).getValue();
            if (!todayValue.equals(yesterdayValue)) {
                lastDetailValue = todayValue - yesterdayValue;
            }
        }
        return lastDetailValue;
    }

    private Double getLastValue(List<ItemDetail> itemDetails) {
        if (itemDetails.isEmpty()) {
            return 0.0;
        }
        return itemDetails.get(0).getValue();
    }

    private String redirectToRoot() {
        return redirectTo("/");
    }

    private String redirectTo(String url) {
        return "redirect:" + url;
    }
}
