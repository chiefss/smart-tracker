package org.chiefss.smarttracker.integration.dao;


import org.chiefss.smarttracker.application.entities.ItemDetail;
import org.chiefss.smarttracker.application.repository.ItemDetailDao;
import org.chiefss.smarttracker.integration.AbstractIntegrationSpringBootTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

class TestItemDetailDao extends AbstractIntegrationSpringBootTest {

    private final ItemDetailDao itemDetailDao;

    @Autowired
    TestItemDetailDao(ItemDetailDao itemDetailDao) {
        this.itemDetailDao = itemDetailDao;
    }

    @Test
    void testFindById() {
        Optional<ItemDetail> itemDetailOptional = itemDetailDao.findById(1L);

        Assertions.assertTrue(itemDetailOptional.isPresent());
        ItemDetail itemDetail = itemDetailOptional.get();
        Assertions.assertEquals(1L, itemDetail.getId());
    }

    @Test
    void findAllByItemId_WithoutLimit() {
        List<ItemDetail> itemDetails = itemDetailDao.findAllByItemId(1L, null);

        Assertions.assertFalse(itemDetails.isEmpty());
    }

    @Test
    void findAllByItemId_WithLimit() {
        List<ItemDetail> itemDetails = itemDetailDao.findAllByItemId(1L, 2);

        Assertions.assertEquals(2, itemDetails.size());
    }

    @Test
    void testDelete() {
        Long itemId = 3L;

        itemDetailDao.deleteAllByItemId(itemId);

        List<ItemDetail> items = itemDetailDao.findAll();
        Assertions.assertTrue(items.stream().noneMatch(itemDetail -> itemDetail.getItem().getId().equals(itemId)));
    }
}
