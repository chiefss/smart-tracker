package org.devel.smarttracker.functional.services;


import javassist.NotFoundException;
import org.devel.smarttracker.application.dto.internal.ItemDetailViewDto;
import org.devel.smarttracker.application.utils.CurrencyUtils;
import org.devel.smarttracker.functional.AbstractFunctionalTest;
import org.devel.smarttracker.application.dto.ItemDetailDto;
import org.devel.smarttracker.application.entities.Item;
import org.devel.smarttracker.application.entities.ItemDetail;
import org.devel.smarttracker.application.repository.ItemDao;
import org.devel.smarttracker.application.repository.ItemDetailDao;
import org.devel.smarttracker.application.services.ItemDetailService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        Item itemEntity = new Item();
        itemEntity.setId(2L);
        when(itemDetailDao.findAllByItemId(itemEntity.getId(), 2))
                .thenReturn(List.of());

        itemDetailService.findAll(itemEntity.getId(), 2);

        verify(itemDetailDao, times(1)).findAllByItemId(itemEntity.getId(), 2);
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
        assertEquals(itemId, createdItem.getItem().getId());
        assertEquals(222.3, createdItem.getValue());
    }

    @Test
    void testFindAllItemDetailDtoByItemId() throws NotFoundException {
        Long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        ItemDetail itemDetail1 = new ItemDetail(item, 10.0);
        ItemDetail itemDetail2 = new ItemDetail(item, 20.0);
        List<ItemDetail> itemDetails = Arrays.asList(itemDetail1, itemDetail2);
        when(itemDao.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemDetailDao.findAllByItemId(itemId, null)).thenReturn(itemDetails);
        try (MockedStatic<CurrencyUtils> currencyUtilsMockedStatic = mockStatic(CurrencyUtils.class)) {
            currencyUtilsMockedStatic.when(() -> CurrencyUtils.formatCurrency(10.0)).thenReturn("10.00");
            currencyUtilsMockedStatic.when(() -> CurrencyUtils.formatCurrency(20.0)).thenReturn("20.00");

            List<ItemDetailDto> result = itemDetailService.findAllItemDetailDtoByItemId(itemId);

            assertEquals(2, result.size());
            assertEquals("10.00", result.get(0).getFormattedValue());
            assertEquals("20.00", result.get(1).getFormattedValue());
            verify(itemDetailDao, times(1)).findAllByItemId(itemId, null);
        }
    }

    @Test
    void testFindAllItemDetailViewByItemId() throws NotFoundException {
        Long itemId = 1L;
        Integer limit = 5;
        Item item = new Item();
        item.setId(itemId);
        when(itemDao.findById(anyLong())).thenReturn(Optional.of(item));
        ItemDetail itemDetail1 = new ItemDetail(item, 10.0);
        ItemDetail itemDetail2 = new ItemDetail(item, 20.0);
        List<ItemDetail> itemDetails = Arrays.asList(itemDetail1, itemDetail2);
        when(itemDetailDao.findAllByItemId(itemId, limit)).thenReturn(itemDetails);
        try (MockedStatic<CurrencyUtils> currencyUtilsMockedStatic = mockStatic(CurrencyUtils.class)) {
            currencyUtilsMockedStatic.when(() -> CurrencyUtils.formatCurrency(10.0)).thenReturn("10.00");
            currencyUtilsMockedStatic.when(() -> CurrencyUtils.formatCurrency(20.0)).thenReturn("20.00");

            List<ItemDetailViewDto> result = itemDetailService.findAllItemDetailViewByItemId(itemId, limit);

            assertEquals(2, result.size());
            assertEquals("10.00", result.get(0).getFormattedValue());
            assertEquals("20.00", result.get(1).getFormattedValue());
            verify(itemDetailDao, times(1)).findAllByItemId(itemId, limit);
        }
    }

    @Test
    void testCleanDetailDuplicates_NoDuplicates() throws NotFoundException {
        Long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        when(itemDao.findById(anyLong())).thenReturn(Optional.of(item));
        ItemDetail detail1 = new ItemDetail(item, 10.0);
        ItemDetail detail2 = new ItemDetail(item, 20.0);
        List<ItemDetail> itemDetails = Arrays.asList(detail1, detail2);
        when(itemDetailDao.findAllByItemId(itemId, null)).thenReturn(itemDetails);

        itemDetailService.cleanDetailDuplicates(itemId);

        verify(itemDetailDao, never()).deleteById(anyLong());
    }

    @Test
    void testCleanDetailDuplicates_WithDuplicates() throws NotFoundException {
        Long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        when(itemDao.findById(anyLong())).thenReturn(Optional.of(item));
        ItemDetail detail1 = new ItemDetail(item, 10.0);
        detail1.setId(1L);
        ItemDetail detail2 = new ItemDetail(item, 10.0);
        detail2.setId(2L);
        ItemDetail detail3 = new ItemDetail(item, 20.0);
        detail3.setId(3L);
        List<ItemDetail> itemDetails = Arrays.asList(detail1, detail2, detail3);
        when(itemDetailDao.findById(1L)).thenReturn(Optional.of(detail1));
        when(itemDetailDao.findById(2L)).thenReturn(Optional.of(detail2));
        when(itemDetailDao.findById(3L)).thenReturn(Optional.of(detail3));
        when(itemDetailDao.findAllByItemId(itemId, null)).thenReturn(itemDetails);

        itemDetailService.cleanDetailDuplicates(itemId);

        verify(itemDetailDao, times(1)).deleteById(detail2.getId());
        verify(itemDetailDao, never()).deleteById(detail1.getId());
        verify(itemDetailDao, never()).deleteById(detail3.getId());
    }

    @Test
    void testCleanDetailDuplicates_AllDuplicates() throws NotFoundException {
        Long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        when(itemDao.findById(anyLong())).thenReturn(Optional.of(item));
        ItemDetail detail1 = new ItemDetail(item, 10.0);
        detail1.setId(1L);
        ItemDetail detail2 = new ItemDetail(item, 10.0);
        detail2.setId(2L);
        ItemDetail detail3 = new ItemDetail(item, 10.0);
        detail3.setId(3L);
        List<ItemDetail> itemDetails = Arrays.asList(detail1, detail2, detail3);
        when(itemDetailDao.findById(1L)).thenReturn(Optional.of(detail1));
        when(itemDetailDao.findById(2L)).thenReturn(Optional.of(detail2));
        when(itemDetailDao.findById(3L)).thenReturn(Optional.of(detail3));
        when(itemDetailDao.findAllByItemId(itemId, null)).thenReturn(itemDetails);

        itemDetailService.cleanDetailDuplicates(itemId);

        verify(itemDetailDao, times(2)).deleteById(anyLong());
        verify(itemDetailDao, never()).deleteById(detail1.getId());
        verify(itemDetailDao, times(1)).deleteById(detail2.getId());
        verify(itemDetailDao, times(1)).deleteById(detail3.getId());
    }
}
