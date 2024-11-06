package org.chiefss.smarttracker.application.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.chiefss.smarttracker.application.dto.EmailMessageDto;
import org.chiefss.smarttracker.application.dto.ItemDetailParserResultDto;
import org.chiefss.smarttracker.application.entities.Item;
import org.chiefss.smarttracker.application.entities.ItemDetail;
import org.chiefss.smarttracker.application.utils.Counter;
import org.chiefss.smarttracker.application.utils.CurrencyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.StringJoiner;

@Log4j2
@Service
@RequiredArgsConstructor
public class ItemDetailParserNotifierService {

    public static final String MESSAGE_SUBJECT_DELIMITER = ", ";
    public static final String MESSAGE_PARAGRAPH_DELIMITER = "\n\n";
    public static final int FIND_ITEM_DETAIL_LIMIT = 2;
    private final ItemDetailService itemDetailService;
    private final MailService mailService;

    @Transactional(readOnly = true)
    public void notify(List<ItemDetailParserResultDto> itemDetailParserResultDtos) {
        StringJoiner successBody = new StringJoiner(MESSAGE_PARAGRAPH_DELIMITER);
        StringJoiner failBody = new StringJoiner(MESSAGE_PARAGRAPH_DELIMITER);
        Counter successCounter = new Counter();
        Counter failCounter = new Counter();
        for (ItemDetailParserResultDto itemDetailParserResultDto : itemDetailParserResultDtos) {
            handleResultDto(itemDetailParserResultDto, successBody, failBody, successCounter, failCounter);
        }
        sendNotificationIfNeeded(successBody, failBody, successCounter, failCounter);
    }

    private void sendNotificationIfNeeded(StringJoiner successBody, StringJoiner failBody, Counter successCounter, Counter failCounter) {
        if (successCounter.isNotEmpty() || failCounter.isNotEmpty()) {
            EmailMessageDto emailMessageDto = buildEmailMessage(successCounter, successBody, failCounter, failBody);
            mailService.sendAdmin(emailMessageDto.getSubject(), emailMessageDto.getBody());
        }
    }

    private void handleResultDto(ItemDetailParserResultDto itemDetailParserResultDto, StringJoiner successBody, StringJoiner failBody, Counter successCounter, Counter failCounter) {
        Item item = itemDetailParserResultDto.getItem();
        if (itemDetailParserResultDto.isSuccess()) {
            handleSuccess(successBody, successCounter, item);
        } else {
            handleFailure(failBody, failCounter, item, itemDetailParserResultDto.getMessage());
        }
    }

    private void handleSuccess(StringJoiner successBody, Counter successCounter, Item item) {
        List<ItemDetail> itemDetails = itemDetailService.findAll(item.getId(), FIND_ITEM_DETAIL_LIMIT);
        if (isValueReduced(itemDetails)) {
            addSuccessMessage(successBody, item, itemDetails);
            successCounter.increase();
        }
    }

    private void addSuccessMessage(StringJoiner successBody, Item item, List<ItemDetail> itemDetails) {
        String formatSuccessMessage = formatSuccessMessage(item, itemDetails.get(0).getValue(), itemDetails.get(1).getValue());
        successBody.add(formatSuccessMessage);
    }

    private String formatSuccessMessage(Item item, double itemDetailValueCurrent, double itemDetailValuePrev) {
        return String.format("\"%s\" with id \"%d\" change value to %s (-%s) url \"%s\"",
                item.getName(), item.getId(),
                CurrencyUtils.formatCurrency(itemDetailValueCurrent),
                CurrencyUtils.formatCurrency(itemDetailValuePrev - itemDetailValueCurrent),
                item.getUrl());
    }

    private void handleFailure(StringJoiner failBody, Counter failCounter, Item item, String message) {
        addFailureMessage(failBody, item, message);
        failCounter.increase();
    }

    private void addFailureMessage(StringJoiner failBody, Item item, String message) {
        String formatFailureMessage = formatFailureMessage(item, message);
        failBody.add(formatFailureMessage);
    }

    private String formatFailureMessage(Item item, String message) {
        return String.format("\"%s\" with id \"%d\", error: \"%s\"", item.getName(), item.getId(), message);
    }

    private EmailMessageDto buildEmailMessage(Counter successCount, StringJoiner successBody, Counter failCount, StringJoiner failBody) {
        String subject = buildEmailMessageSubject(successCount, failCount);
        String body = buildEmailMessageBody(successCount, successBody, failCount, failBody);
        return new EmailMessageDto(subject, body);
    }

    private String buildEmailMessageSubject(Counter successCount, Counter failCount) {
        StringJoiner subject = new StringJoiner(MESSAGE_SUBJECT_DELIMITER);
        subject.add(String.format("[Smart tracker] Parser reporting. Reduced %d", successCount.getCount()));
        if (failCount.isNotEmpty()) {
            subject.add(String.format("errors %d", failCount.getCount()));
        }
        return subject.toString();
    }

    private String buildEmailMessageBody(Counter successCount, StringJoiner successBody, Counter failCount, StringJoiner failBody) {
        StringJoiner body = new StringJoiner(MESSAGE_PARAGRAPH_DELIMITER);
        if (successCount.isNotEmpty()) {
            body.add(buildSuccessBody(successBody));
        }
        if (failCount.isNotEmpty()) {
            body.add(buildFailBody(failBody));
        }
        return body.toString();
    }

    private boolean isValueReduced(List<ItemDetail> itemDetails) {
        if (!itemDetails.isEmpty()) {
            double currentValue = itemDetails.get(0).getValue();
            double prevValue = itemDetails.get(1).getValue();
            return currentValue < prevValue;
        }
        return false;
    }

    private String buildSuccessBody(StringJoiner successBody) {
        return String.join(MESSAGE_PARAGRAPH_DELIMITER, "Value reduced:", successBody.toString());
    }

    private String buildFailBody(StringJoiner failBody) {
        return String.join(MESSAGE_PARAGRAPH_DELIMITER, "Errors:", failBody.toString());
    }
}
