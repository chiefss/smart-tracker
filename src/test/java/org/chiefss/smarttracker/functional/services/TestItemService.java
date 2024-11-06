package org.chiefss.smarttracker.functional.services;


import org.chiefss.smarttracker.application.dto.ItemCreateDto;
import org.chiefss.smarttracker.application.dto.ItemUpdateDto;
import org.chiefss.smarttracker.application.entities.Item;
import org.chiefss.smarttracker.application.repository.ItemDao;
import org.chiefss.smarttracker.application.repository.ItemDetailDao;
import org.chiefss.smarttracker.application.services.ItemService;
import org.chiefss.smarttracker.functional.AbstractFunctionalTest;
import javassist.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class TestItemService extends AbstractFunctionalTest {

    private final ItemService itemService;

    private final ItemDao itemDao;

    private final ItemDetailDao itemDetailDao;

    public TestItemService() {
        this.itemDao = mock(ItemDao.class);
        this.itemDetailDao = mock(ItemDetailDao.class);
        this.itemService = new ItemService(itemDao, itemDetailDao);
    }

    @Test
    void testFind() throws NotFoundException {
        when(itemDao.findById(1L))
                .thenReturn(Optional.of(new Item()));

        itemService.findById(1L);

        verify(itemDao, times(1)).findById(1L);
    }

    @Test
    void testFindNotFound() {
        when(itemDao.findById(1000L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> itemService.findById(1000L));

        verify(itemDao, times(1)).findById(1000L);
    }

    @Test
    void testCreate() {
        ItemCreateDto itemCreateDto = new ItemCreateDto("name 6", "url 6", "selector 6", "break selector 6");

        itemService.create(itemCreateDto);

        ArgumentCaptor<Item> requestCaptorItem = ArgumentCaptor.forClass(Item.class);
        verify(itemDao, times(1)).save(requestCaptorItem.capture());
        Item item = requestCaptorItem.getValue();
        Assertions.assertEquals("name 6", item.getName());
        Assertions.assertEquals("url 6", item.getUrl());
        Assertions.assertEquals("selector 6", item.getSelector());
        Assertions.assertEquals("break selector 6", item.getBreakSelector());
    }

    @Test
    void testUpdate() throws NotFoundException {
        Long itemId = 2L;
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(itemId, "name 2-2", "url 2-2", "selector 2-2", "break selector 2-2");
        Item item = new Item("name 2", "url 2", "selector 2", "break selector 2");
        item.setId(itemId);
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item));

        itemService.update(itemUpdateDto);

        ArgumentCaptor<Item> requestCaptorItem = ArgumentCaptor.forClass(Item.class);
        verify(itemDao, times(1)).save(requestCaptorItem.capture());
        Item updatedItem = requestCaptorItem.getValue();
        Assertions.assertEquals("name 2-2", updatedItem.getName());
        Assertions.assertEquals("url 2-2", updatedItem.getUrl());
        Assertions.assertEquals("selector 2-2", updatedItem.getSelector());
        Assertions.assertEquals("break selector 2-2", updatedItem.getBreakSelector());
    }

    @Test
    void testUpdateNotFound() {
        when(itemDao.findById(any()))
                .thenReturn(Optional.empty());
        Long expectedId = 1000L;
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(expectedId, "name 2-2", "url 2-2", "selector 2-2", "break selector 2-2");

        Assertions.assertThrows(NotFoundException.class, () -> itemService.update(itemUpdateDto));
    }

    @Test
    void testDelete() throws NotFoundException {
        Long itemId = 3L;
        Item item = new Item();
        item.setId(itemId);
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item));

        itemService.delete(itemId);

        verify(itemDetailDao, times(1)).deleteAllByItemId(itemId);
        verify(itemDao, times(1)).deleteById(itemId);
    }

    @Test
    void testDeleteNotFound() {
        Long itemId = 1000L;

        Assertions.assertThrows(NotFoundException.class, () -> itemService.delete(itemId));
    }
}
