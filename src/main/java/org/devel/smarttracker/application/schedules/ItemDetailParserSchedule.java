package org.devel.smarttracker.application.schedules;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.devel.smarttracker.application.dto.ItemDetailParserResultDto;
import org.devel.smarttracker.application.services.ItemDetailParserNotifierService;
import org.devel.smarttracker.application.services.ItemDetailParserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@Log4j2
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.parser.cron.enabled", havingValue = "true")
public class ItemDetailParserSchedule {

    private final ItemDetailParserService itemDetailParserService;
    private final ItemDetailParserNotifierService itemDetailParserNotifierService;

    @Scheduled(fixedRateString = "${app.parser.cron.interval}", initialDelayString = "${app.parser.cron.initial-timeout}")
    public void start() {
        List<ItemDetailParserResultDto> itemDetailParserResultDtos = itemDetailParserService.parseAll();
        itemDetailParserNotifierService.notify(itemDetailParserResultDtos);
    }
}
