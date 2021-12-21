package org.devel.smarttracker.application.services;

import javassist.NotFoundException;
import org.devel.smarttracker.application.dto.ItemDto;
import org.devel.smarttracker.application.entities.Item;
import org.devel.smarttracker.application.repository.ItemDao;
import org.devel.smarttracker.application.repository.ItemDetailDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemService {

    private final ItemDao itemDao;

    private final ItemDetailDao itemDetailDao;

    @Autowired
    public ItemService(ItemDao itemDao, ItemDetailDao itemDetailDao) {
        this.itemDao = itemDao;
        this.itemDetailDao = itemDetailDao;
    }

    @Transactional(readOnly = true)
    public List<Item> findAll(boolean activatedOnly) {
        return itemDao.findAllByActivated(activatedOnly);
    }

    @Transactional(readOnly = true)
    public Item find(Long itemId) throws NotFoundException {
        return itemDao.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Cannot find Item with id: %d", itemId)));
    }

    @Transactional
    public Item create(ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setUrl(itemDto.getUrl());
        item.setSelector(itemDto.getSelector());
        item.setBreakSelector(itemDto.getBreakSelector());
        item.setCreatedAt(LocalDateTime.now());
        return itemDao.save(item);
    }

    @Transactional
    public Item update(ItemDto itemDto) throws NotFoundException {
        Item itemFromRepository = find(itemDto.getId());
        itemFromRepository.setName(itemDto.getName());
        itemFromRepository.setUrl(itemDto.getUrl());
        itemFromRepository.setSelector(itemDto.getSelector());
        itemFromRepository.setBreakSelector(itemDto.getBreakSelector());
        return itemDao.save(itemFromRepository);
    }

    @Transactional
    public void delete(Long itemId) throws NotFoundException {
        Item item = find(itemId);
        itemDetailDao.deleteAllByItemId(item.getId());
        itemDao.deleteById(item.getId());
    }

    @Transactional
    public void activate(Long itemId) throws NotFoundException {
        Item item = find(itemId);
        item.setDeletedAt(null);
        itemDao.save(item);
    }

    @Transactional
    public void deactivate(Long itemId) throws NotFoundException {
        Item item = find(itemId);
        item.setDeletedAt(LocalDateTime.now());
        itemDao.save(item);
    }
}
