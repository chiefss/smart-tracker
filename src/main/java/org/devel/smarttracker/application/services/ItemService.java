package org.devel.smarttracker.application.services;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.devel.smarttracker.application.dto.ItemCreateDto;
import org.devel.smarttracker.application.dto.ItemUpdateDto;
import org.devel.smarttracker.application.dto.internal.ItemViewDto;
import org.devel.smarttracker.application.entities.Item;
import org.devel.smarttracker.application.entities.ItemDetail;
import org.devel.smarttracker.application.mappers.ItemCreateDtoMapper;
import org.devel.smarttracker.application.mappers.ItemUpdateDtoMapper;
import org.devel.smarttracker.application.mappers.ItemViewDtoMapper;
import org.devel.smarttracker.application.repository.ItemDao;
import org.devel.smarttracker.application.repository.ItemDetailDao;
import org.devel.smarttracker.application.utils.CurrencyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemDao itemDao;

    private final ItemDetailDao itemDetailDao;

    @Transactional(readOnly = true)
    public List<Item> findAll() {
        return itemDao.findAll();
    }

    @Transactional(readOnly = true)
    public List<ItemViewDto> findItemViewDtoAll() {
        return itemDao.findAll().stream()
                .map(ItemViewDtoMapper::toDto)
                .map(this::fillDetail)
                .toList();
    }

    @Transactional(readOnly = true)
    public Item findById(Long itemId) throws NotFoundException {
        return itemDao.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Cannot find Item with id: %d", itemId)));
    }

    @Transactional(readOnly = true)
    public ItemViewDto findByIdItemView(Long itemId) throws NotFoundException {
        Item item = itemDao.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Cannot find Item with id: %d", itemId)));
        return fillDetail(ItemViewDtoMapper.toDto(item));
    }

    @Transactional
    public Item create(ItemCreateDto itemCreateDto) {
        Item item = ItemCreateDtoMapper.toEntity(itemCreateDto);
        return itemDao.save(item);
    }

    @Transactional
    public Item update(ItemUpdateDto itemUpdateDto) throws NotFoundException {
        Item item = ItemUpdateDtoMapper.toEntity(itemUpdateDto, findById(itemUpdateDto.getId()));
        return itemDao.save(item);
    }

    @Transactional
    public void delete(Long itemId) throws NotFoundException {
        Item item = findById(itemId);
        itemDetailDao.deleteAllByItemId(item.getId());
        itemDao.deleteById(item.getId());
    }

    @Transactional
    public void activate(Long itemId) throws NotFoundException {
        Item item = findById(itemId);
        item.setDeletedAt(null);
        itemDao.save(item);
    }

    @Transactional
    public void deactivate(Long itemId) throws NotFoundException {
        Item item = findById(itemId);
        item.setDeletedAt(LocalDateTime.now());
        itemDao.save(item);
    }

    private ItemViewDto fillDetail(ItemViewDto itemViewDto) {
        List<ItemDetail> itemDetails = itemDetailDao.findAllByItemId(itemViewDto.getId(), 2);

        String formattedLastValue = CurrencyUtils.formatCurrency(getLastValue(itemDetails));
        itemViewDto.setFormattedLastValue(formattedLastValue);

        Double delta = getDelta(itemDetails);
        itemViewDto.setDelta(delta);
        itemViewDto.setFormattedDelta(CurrencyUtils.formatCurrency(Math.abs(delta)));

        try {
            URL url = new URL(itemViewDto.getUrl());
            itemViewDto.setHost(url.getHost());
        } catch (MalformedURLException e) {
            log.debug(String.format("Cannot parse host by item entity with url \"%s\"", itemViewDto.getUrl()));
        }

        return itemViewDto;
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
}
