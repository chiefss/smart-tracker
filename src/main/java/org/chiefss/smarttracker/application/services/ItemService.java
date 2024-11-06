package org.chiefss.smarttracker.application.services;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.chiefss.smarttracker.application.dto.ItemCreateDto;
import org.chiefss.smarttracker.application.dto.ItemUpdateDto;
import org.chiefss.smarttracker.application.dto.internal.ItemViewDto;
import org.chiefss.smarttracker.application.entities.Item;
import org.chiefss.smarttracker.application.mappers.ItemCreateDtoMapper;
import org.chiefss.smarttracker.application.mappers.ItemUpdateDtoMapper;
import org.chiefss.smarttracker.application.mappers.ItemViewDtoMapper;
import org.chiefss.smarttracker.application.repository.ItemDao;
import org.chiefss.smarttracker.application.repository.ItemDetailDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemDao itemDao;

    private final ItemDetailDao itemDetailDao;

    @Transactional(readOnly = true)
    public Item findById(Long itemId) throws NotFoundException {
        return itemDao.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Cannot find Item by id: " + itemId));
    }

    @Transactional(readOnly = true)
    public ItemViewDto findByIdItemView(Long itemId) throws NotFoundException {
        Item item = itemDao.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Cannot find Item by id: " + itemId));
        return ItemViewDtoMapper.toDto(item, this.itemDetailDao.findAllByItemId(item.getId(), 2));
    }

    @Transactional(readOnly = true)
    public List<Item> findAll() {
        return itemDao.findAll();
    }

    @Transactional(readOnly = true)
    public List<ItemViewDto> findItemViewDtoAll() {
        return itemDao.findAll().stream()
                .map(item -> ItemViewDtoMapper.toDto(item, this.itemDetailDao.findAllByItemId(item.getId(), 2)))
                .toList();
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

    @Transactional
    public void delete(Long itemId) throws NotFoundException {
        Item item = findById(itemId);
        itemDetailDao.deleteAllByItemId(item.getId());
        itemDao.deleteById(item.getId());
    }
}
