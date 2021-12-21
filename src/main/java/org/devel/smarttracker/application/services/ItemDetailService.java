package org.devel.smarttracker.application.services;

import javassist.NotFoundException;
import org.devel.smarttracker.application.dto.ItemDetailDto;
import org.devel.smarttracker.application.entities.Item;
import org.devel.smarttracker.application.entities.ItemDetail;
import org.devel.smarttracker.application.repository.ItemDao;
import org.devel.smarttracker.application.repository.ItemDetailDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemDetailService {

    private final ItemDetailDao itemDetailDao;

    private final ItemDao itemDao;

    @Autowired
    public ItemDetailService(ItemDetailDao itemDetailDao, ItemDao itemDao) {
        this.itemDetailDao = itemDetailDao;
        this.itemDao = itemDao;
    }

    @Transactional(readOnly = true)
    public List<ItemDetail> findAll(Item item) {
        return itemDetailDao.findAllByItemId(item.getId(), null);
    }

    @Transactional(readOnly = true)
    public List<ItemDetail> findLast(Item item) {
        return itemDetailDao.findAllByItemId(item.getId(), 2);
    }

    @Transactional
    public ItemDetail create(ItemDetailDto itemDetailDto) throws NotFoundException {
        Long itemId = itemDetailDto.getItemId();
        Item item = itemDao.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Cannot create Item detail and find Item with id: %d", itemId)));
        ItemDetail itemDetail = new ItemDetail();
        itemDetail.setItem(item);
        itemDetail.setValue(itemDetailDto.getValue());
        itemDetail.setCreatedAt(LocalDateTime.now());
        return itemDetailDao.save(itemDetail);
    }

    @Transactional
    public void delete(Long id) {
        itemDetailDao.deleteById(id);
    }
}
