package org.devel.smarttracker.application.schedules;

import javassist.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.devel.smarttracker.application.dto.EmailMessage;
import org.devel.smarttracker.application.dto.ItemDetailDto;
import org.devel.smarttracker.application.dto.ItemDetailParserResultDto;
import org.devel.smarttracker.application.entities.Item;
import org.devel.smarttracker.application.entities.ItemDetail;
import org.devel.smarttracker.application.factory.ItemDetailDtoFactory;
import org.devel.smarttracker.application.parsers.ItemDetailParser;
import org.devel.smarttracker.application.services.ItemDetailService;
import org.devel.smarttracker.application.services.MailService;
import org.devel.smarttracker.application.utils.Counter;
import org.devel.smarttracker.application.utils.CurrencyUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.StringJoiner;


@Log4j2
@Component
@ConditionalOnProperty(name = "app.parser.cron.enabled", havingValue = "true")
public class ItemDetailParserSchedule {

    public static final String SUBJECT_DELIMITER = ", ";
    public static final String SUCCESS_BODY_TITLE_DELIMITER = "\n\n";
    public static final String SUCCESS_BODY_DELIMITER = "\n\n";
    public static final String BODY_DELIMITER = "\n\n";
    public static final String FAIL_BODY_TITLE_DELIMITER = "\n\n";
    public static final String FAIL_BODY_DELIMITER = "\n\n";

    private final ItemDetailParser itemDetailParser;

    private final ItemDetailService itemDetailService;

    private final MailService mailService;

    public ItemDetailParserSchedule(ItemDetailParser itemDetailParser, ItemDetailService itemDetailService, MailService mailService) {
        this.itemDetailParser = itemDetailParser;
        this.itemDetailService = itemDetailService;
        this.mailService = mailService;
    }

    @Scheduled(fixedRateString = "${app.parser.cron.interval}", initialDelayString = "${app.parser.cron.initial-timeout}")
    public void start() {
        List<ItemDetailParserResultDto> itemDetailParserResultDtos = itemDetailParser.parseAll();
        saveItemDetailAll(itemDetailParserResultDtos);
        notify(itemDetailParserResultDtos);
    }

    private void saveItemDetailAll(List<ItemDetailParserResultDto> itemDetailParserResultDtos) {
        for (ItemDetailParserResultDto itemDetailParserResultDto : itemDetailParserResultDtos) {
            if (!itemDetailParserResultDto.isSuccess()) {
                continue;
            }
            try {
                saveItemDetail(itemDetailParserResultDto);
            } catch (NotFoundException e) {
                log.error(String.format("Cannot parse all by schedule and save item detail for item with id \"%d\"", itemDetailParserResultDto.getItem().getId()));
            }
        }
    }

    private void saveItemDetail(ItemDetailParserResultDto itemDetailParserResultDto) throws NotFoundException {
        ItemDetail itemDetail = itemDetailParserResultDto.getItemDetail();
        ItemDetailDto itemDetailDto = ItemDetailDtoFactory.create(itemDetail.getItem().getId(), itemDetail.getValue());
        itemDetailService.create(itemDetailDto);
    }

    private void notify(List<ItemDetailParserResultDto> itemDetailParserResultDtos) {
        StringJoiner successBody = new StringJoiner(SUCCESS_BODY_DELIMITER);
        StringJoiner failBody = new StringJoiner(FAIL_BODY_DELIMITER);
        Counter successCounter = new Counter();
        Counter failCounter = new Counter();
        for (ItemDetailParserResultDto itemDetailParserResultDto : itemDetailParserResultDtos) {
            Item item = itemDetailParserResultDto.getItem();
            if (itemDetailParserResultDto.isSuccess()) {
                List<ItemDetail> itemDetails = itemDetailService.findLast(item);
                if (!isValueReduced(itemDetails)) {
                    continue;
                }
                double itemDetailValueCurrent = itemDetails.get(0).getValue();
                double itemDetailValuePrev = itemDetails.get(1).getValue();
                successBody.add(String.format("\"%s\" with id \"%d\" change value to %s (-%s) url \"%s\"",
                        item.getName(), item.getId(),
                        CurrencyUtils.formatCurrency(itemDetailValueCurrent),
                        CurrencyUtils.formatCurrency(itemDetailValuePrev - itemDetailValueCurrent),
                        item.getUrl()));
                successCounter.increase();
            } else {
                failBody.add(String.format("\"%s\" with id \"%d\", error: \"%s\"", item.getName(), item.getId(), itemDetailParserResultDto.getMessage()));
                failCounter.increase();
            }
        }
        int successCount = successCounter.getCount();
        int failCount = failCounter.getCount();
        if (successCount > 0 || failCount > 0) {
            EmailMessage emailMessage = buildEmailMessage(successCount, successBody, failCount, failBody);
            mailService.sendAdmin(emailMessage.getSubject(), emailMessage.getBody());
        }
    }

    private EmailMessage buildEmailMessage(int successCount, StringJoiner successBody, int failCount, StringJoiner failBody) {
        StringJoiner subject = new StringJoiner(SUBJECT_DELIMITER);
        StringJoiner body = new StringJoiner(BODY_DELIMITER);
        subject.add(String.format("[Smart tracker] Parser reporting. Reduced %d", successCount));
        if (successCount > 0) {
            body.add(buildSuccessBody(successBody));
        }
        if (failCount > 0) {
            subject.add(String.format("errors %d", failCount));
            body.add(buildFailBody(failBody));
        }
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setSubject(subject.toString());
        emailMessage.setBody(body.toString());
        return emailMessage;
    }

    private boolean isValueReduced(List<ItemDetail> itemDetails) {
        if (itemDetails.isEmpty()) {
            return false;
        }
        double currentValue = itemDetails.get(0).getValue();
        double prevValue = itemDetails.get(1).getValue();
        return currentValue < prevValue;
    }

    private String buildSuccessBody(StringJoiner successBody) {
        return String.join(SUCCESS_BODY_TITLE_DELIMITER, "Value reduced:", successBody.toString());
    }

    private String buildFailBody(StringJoiner failBody) {
        return String.join(FAIL_BODY_TITLE_DELIMITER, "Errors:", failBody.toString());
    }
}
