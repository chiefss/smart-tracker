package org.devel.smarttracker.application.services;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class ItemDetailService {

    private final ItemDetailDao itemDetailDao;

    private final ItemDao itemDao;

    @Transactional(readOnly = true)
    public List<ItemDetail> findAll(Long itemId, Integer limit) {
        return itemDetailDao.findAllByItemId(itemId, limit);
    }

    @Transactional(readOnly = true)
    public List<ItemDetailDto> findAllItemDetailDtoByItemId(Long itemId) {
        return itemDetailDao.findAllByItemId(itemId, null)
                .stream()
                .map(itemDetail -> {
                    ItemDetailDto itemDetailDto = ItemDetailDtoMapper.toDto(itemDetail);
                    itemDetailDto.setFormattedValue(CurrencyUtils.formatCurrency(itemDetailDto.getValue()));
                    return itemDetailDto;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ItemDetailViewDto> findAllItemDetailViewByItemId(Long itemId, Integer limit) {
        return itemDetailDao.findAllByItemId(itemId, limit).stream()
                .map(itemDetail -> {
                    ItemDetailViewDto itemDetailViewDto = ItemDetailViewDtoMapper.toDto(itemDetail);
                    itemDetailViewDto.setFormattedValue(CurrencyUtils.formatCurrency(itemDetailViewDto.getValue()));
                    return itemDetailViewDto;
                })
                .toList();
    }

    @Transactional
    public void cleanDetailDuplicates(Long itemId) {
        List<ItemDetail> itemDetails = itemDetailDao.findAllByItemId(itemId, null);
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
        Item item = itemDao.findById(itemDetailDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("Cannot find Item with id: %d and create ItemDetail", itemDetailDto.getItemId())));
        ItemDetail itemDetail = ItemDetailDtoMapper.toEntity(itemDetailDto, item);
        return itemDetailDao.save(itemDetail);
    }

    @Transactional
    public void delete(Long id) {
        itemDetailDao.deleteById(id);
    }
}
