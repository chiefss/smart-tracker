package org.chiefss.smarttracker.application.services;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.chiefss.smarttracker.application.dto.ItemDetailDto;
import org.chiefss.smarttracker.application.dto.ItemDetailParserResultDto;
import org.chiefss.smarttracker.application.entities.Item;
import org.chiefss.smarttracker.application.entities.ItemDetail;
import org.chiefss.smarttracker.application.mappers.ItemDetailDtoMapper;
import org.chiefss.smarttracker.application.parsers.ItemDetailParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class ItemDetailParserService {

    private final ItemDetailParser itemDetailParser;
    private final ItemDetailService itemDetailService;

    @Transactional
    public void parse(Item createdItem) {
        try {
            Optional<ItemDetail> itemDetailOptional = itemDetailParser.parse(createdItem);
            if (itemDetailOptional.isEmpty()) {
                throw new NotFoundException("ItemDetail is empty");
            }
            saveItemDetail(itemDetailOptional.get());
        } catch (NotFoundException | IOException e) {
            log.error("Cannot parse item detail from url {}: {}", createdItem.getUrl(), e.getMessage());
        }
    }

    @Transactional
    public List<ItemDetailParserResultDto> parseAll() {
        List<ItemDetailParserResultDto> itemDetailParserResultDtos = itemDetailParser.parseAll();
        for (ItemDetailParserResultDto itemDetailParserResultDto : itemDetailParserResultDtos) {
            if (!itemDetailParserResultDto.isSuccess()) {
                continue;
            }
            saveItemDetail(itemDetailParserResultDto.getItemDetail());
        }
        return itemDetailParserResultDtos;
    }

    private void saveItemDetail(ItemDetail itemDetail) {
        ItemDetailDto itemDetailDto = ItemDetailDtoMapper.toDto(itemDetail.getItem().getId(), itemDetail.getValue());
        try {
            itemDetailService.create(itemDetailDto);
        } catch (NotFoundException e) {
            log.error("Cannot save item detail for item id \"{}\": {}", itemDetail.getItem().getId(), e.getMessage());
        }
    }
}
