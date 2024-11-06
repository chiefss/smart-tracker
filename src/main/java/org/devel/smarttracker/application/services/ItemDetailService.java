package org.devel.smarttracker.application.services;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.devel.smarttracker.application.dto.ItemDetailDto;
import org.devel.smarttracker.application.dto.internal.ItemDetailViewDto;
import org.devel.smarttracker.application.entities.Item;
import org.devel.smarttracker.application.entities.ItemDetail;
import org.devel.smarttracker.application.mappers.ItemDetailDtoMapper;
import org.devel.smarttracker.application.mappers.ItemDetailViewDtoMapper;
import org.devel.smarttracker.application.repository.ItemDao;
import org.devel.smarttracker.application.repository.ItemDetailDao;
import org.devel.smarttracker.application.utils.CurrencyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ItemDetailService {

    private final ItemDetailDao itemDetailDao;

    private final ItemDao itemDao;

    @Transactional(readOnly = true)
    public ItemDetail findById(Long itemDetailId) throws NotFoundException {
        return itemDetailDao.findById(itemDetailId)
                .orElseThrow(() -> new NotFoundException("Cannot find ItemDetail by id: " + itemDetailId));
    }

    @Transactional(readOnly = true)
    public List<ItemDetail> findAll(Long itemId, Integer limit) {
        return itemDetailDao.findAllByItemId(itemId, limit);
    }

    @Transactional(readOnly = true)
    public List<ItemDetailDto> findAllItemDetailDtoByItemId(Long itemId) throws NotFoundException {
        Item item = findItemById(itemId);
        return itemDetailDao.findAllByItemId(item.getId(), null)
                .stream()
                .map(itemDetail -> {
                    ItemDetailDto itemDetailDto = ItemDetailDtoMapper.toDto(itemDetail);
                    itemDetailDto.setFormattedValue(CurrencyUtils.formatCurrency(itemDetailDto.getValue()));
                    return itemDetailDto;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ItemDetailViewDto> findAllItemDetailViewByItemId(Long itemId, Integer limit) throws NotFoundException {
        Item item = findItemById(itemId);
        return itemDetailDao.findAllByItemId(item.getId(), limit).stream()
                .map(itemDetail -> {
                    ItemDetailViewDto itemDetailViewDto = ItemDetailViewDtoMapper.toDto(itemDetail);
                    itemDetailViewDto.setFormattedValue(CurrencyUtils.formatCurrency(itemDetailViewDto.getValue()));
                    return itemDetailViewDto;
                })
                .toList();
    }

    @Transactional
    public void cleanDetailDuplicates(Long itemId) throws NotFoundException {
        Item item = findItemById(itemId);
        List<ItemDetail> itemDetails = itemDetailDao.findAllByItemId(item.getId(), null);
        Double prevValue = null;
        for (ItemDetail itemDetail : itemDetails) {
            Double currentValue = itemDetail.getValue();
            if (currentValue.equals(prevValue)) {
                delete(itemDetail.getId());
            }
            prevValue = currentValue;
        }
    }

    @Transactional
    public ItemDetail create(ItemDetailDto itemDetailDto) throws NotFoundException {
        Item item = findItemById(itemDetailDto.getItemId());
        ItemDetail itemDetail = ItemDetailDtoMapper.toEntity(itemDetailDto, item);
        return itemDetailDao.save(itemDetail);
    }

    @Transactional
    public void delete(Long itemDetailId) throws NotFoundException {
        ItemDetail itemDetail = findById(itemDetailId);
        itemDetailDao.deleteById(itemDetail.getId());
    }

    private Item findItemById(Long itemId) throws NotFoundException {
        return itemDao.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Cannot find Item by id: " + itemId));
    }
}
