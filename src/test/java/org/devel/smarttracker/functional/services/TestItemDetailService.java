package org.devel.smarttracker.functional.services;


import javassist.NotFoundException;
import org.devel.smarttracker.functional.AbstractFunctionalTest;
import org.devel.smarttracker.application.dto.ItemDetailDto;
import org.devel.smarttracker.application.entities.Item;
import org.devel.smarttracker.application.entities.ItemDetail;
import org.devel.smarttracker.application.repository.ItemDao;
import org.devel.smarttracker.application.repository.ItemDetailDao;
import org.devel.smarttracker.application.services.ItemDetailService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class TestItemDetailService extends AbstractFunctionalTest {

    private final ItemDetailService itemDetailService;

    private final ItemDao itemDao;

    private final ItemDetailDao itemDetailDao;

    TestItemDetailService() {
        this.itemDao = mock(ItemDao.class);
        this.itemDetailDao = mock(ItemDetailDao.class);
        this.itemDetailService = new ItemDetailService(itemDetailDao, itemDao);
    }

    @Test
    void testFindAll() {
        Item itemEnity = new Item();
        itemEnity.setId(1L);
        when(itemDetailDao.findAllByItemId(itemEnity.getId(), null))
                .thenReturn(List.of());

        itemDetailService.findAll(itemEnity);

        verify(itemDetailDao, times(1)).findAllByItemId(itemEnity.getId(), null);
    }

    @Test
    void testFindLast() {
        Item itemEnity = new Item();
        itemEnity.setId(2L);
        when(itemDetailDao.findAllByItemId(itemEnity.getId(), 2))
                .thenReturn(List.of());

        itemDetailService.findLast(itemEnity);

        verify(itemDetailDao, times(1)).findAllByItemId(itemEnity.getId(), 2);
    }

    @Test
    void testCreate() throws NotFoundException {
        long itemId = 1L;
        ItemDetailDto itemDetailDto = new ItemDetailDto(1000L, itemId, 222.3, LocalDateTime.now());
        Item item = new Item();
        item.setId(itemId);
        when(itemDao.findById(itemId))
                .thenReturn(Optional.of(item));

        itemDetailService.create(itemDetailDto);

        ArgumentCaptor<ItemDetail> requestCaptorItem = ArgumentCaptor.forClass(ItemDetail.class);
        verify(itemDetailDao, times(1)).save(requestCaptorItem.capture());
        ItemDetail createdItem = requestCaptorItem.getValue();
        Assertions.assertEquals(itemId, createdItem.getItem().getId());
        Assertions.assertEquals(222.3, createdItem.getValue());
        Assertions.assertNotNull(createdItem.getCreatedAt());
    }
}
