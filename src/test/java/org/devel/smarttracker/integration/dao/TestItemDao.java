package org.devel.smarttracker.integration.dao;


import org.devel.smarttracker.integration.AbstractIntegrationSpringBootTest;
import org.devel.smarttracker.application.entities.Item;
import org.devel.smarttracker.application.repository.ItemDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

class TestItemDao extends AbstractIntegrationSpringBootTest {

    private final ItemDao itemDao;

    @Autowired
    TestItemDao(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    @Test
    void testFindById() {
        Optional<Item> itemEntityOptional = itemDao.findById(1L);

        Assertions.assertTrue(itemEntityOptional.isPresent());
        Item item = itemEntityOptional.get();
        Assertions.assertEquals(1L, item.getId());
    }

    @Test
    void testFindAllActivated() {
        List<Item> items = itemDao.findAllActivated();

        Assertions.assertTrue(items.stream().allMatch(item -> item.getDeletedAt() == null));
    }

    @Test
    void testCreate() {
        Item item = new Item();
        item.setName("name");
        item.setUrl("localhost");
        item.setSelector("");
        item.setBreakSelector("");

        Item createdItem = itemDao.save(item);

        Assertions.assertNotNull(createdItem);
    }
}
