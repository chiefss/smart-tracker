package org.chiefss.smarttracker.application.parsers;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.chiefss.smarttracker.application.configuration.AppParserConnectionHeadersConfig;
import org.chiefss.smarttracker.application.dto.ItemDetailParserResultDto;
import org.chiefss.smarttracker.application.entities.Item;
import org.chiefss.smarttracker.application.entities.ItemDetail;
import org.chiefss.smarttracker.application.repository.ItemDao;
import org.chiefss.smarttracker.application.services.ItemDetailService;
import org.chiefss.smarttracker.application.configuration.AppParserCronConfig;
import org.chiefss.smarttracker.application.factories.ItemDetailFactory;
import org.chiefss.smarttracker.application.mappers.ItemDetailParserResultDtoMapper;
import org.chiefss.smarttracker.application.utils.Defines;
import org.chiefss.smarttracker.application.utils.CurrencyUtils;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;


@Log4j2
@Component
@RequiredArgsConstructor
public class ItemDetailParser {

    public static final String SELECTOR_SPLIT_DELIMITER = "\\|";
    private final ItemDao itemDao;
    private final ItemDetailService itemDetailService;
    private final AppParserConnectionHeadersConfig appParserConnectionHeadersConfig;

    private final AppParserCronConfig appParserCronConfig;

    public List<ItemDetailParserResultDto> parseAll() {
        List<Item> items = itemDao.findAllActivated();
        List<ItemDetailParserResultDto> itemDetailParserResultDtos = new ArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(appParserCronConfig.getMaxThread());
        List<Callable<Optional<ItemDetailParserResultDto>>> tasks = new ArrayList<>();
        for (Item item : items) {
            Callable<Optional<ItemDetailParserResultDto>> parserTask = createParserTask(item);
            tasks.add(parserTask);
        }
        try {
            List<Future<Optional<ItemDetailParserResultDto>>> futures = pool.invokeAll(tasks);
            for (Future<Optional<ItemDetailParserResultDto>> future : futures) {
                future.get().ifPresent(itemDetailParserResultDtos::add);
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error(String.format("Cannot parse all items: \"%s\"", e.getMessage()));
        } finally {
            pool.shutdown();
        }
        return itemDetailParserResultDtos;
    }

    public Optional<ItemDetail> parse(Item item) throws IOException, NotFoundException {
        sleepThread();
        URL itemUrl = new URL(item.getUrl());
        Document itemDocument = loadContent(itemUrl);
        try {
            Double itemDetailValue = findItemDetailValueBySelectors(itemDocument, item.getSelector());
            ItemDetail itemDetail = ItemDetailFactory.create(item, itemDetailValue);
            if (isActualValue(itemDetail)) {
                return Optional.of(itemDetail);
            }
        } catch (NotFoundException e) {
            String breakSelector = item.getBreakSelector();
            if (breakSelector != null && breakSelector.length() > 0 && hasDocumentBreakSelector(itemDocument, breakSelector)) {
                log.info(String.format("Item with id \"%d\" found break selector \"%s\"", item.getId(), breakSelector));
            } else {
                throw e;
            }
        }
        return Optional.empty();
    }

    private void sleepThread() {
        try {
            int sleepTime = ThreadLocalRandom.current().nextInt(Defines.PARSER_THREAD_SLEEP_MIN_SECOND, Defines.PARSER_THREAD_SLEEP_MAX_SECOND + 1) * 1000;
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            log.error(String.format("Cannot sleep parser thread: %s", e.getMessage()));
        }
    }

    private boolean isActualValue(ItemDetail itemDetail) {
        List<ItemDetail> itemDetailList = itemDetailService.findAll(itemDetail.getItem().getId(), 2);
        if (itemDetailList.isEmpty()) {
            return true;
        }
        Double currentValue = itemDetail.getValue();
        Double prevValue = itemDetailList.get(0).getValue();
        return !currentValue.equals(prevValue);
    }

    private Double findItemDetailValueBySelectors(Document document, String selectors) throws NotFoundException {
        for (String selector : selectors.split(SELECTOR_SPLIT_DELIMITER)) {
            Optional<Double> itemDetailValueOptional = findItemDetailValueBySelector(document, selector);
            if (itemDetailValueOptional.isPresent()) {
                return itemDetailValueOptional.get();
            }
        }
        throw new NotFoundException(String.format("Item detail value element not found by selectors \"%s\"", selectors));
    }

    private Optional<Double> findItemDetailValueBySelector(Document document, String selector) {
        try {
            Element element = document.selectFirst(selector);
            if (element == null) {
                throw new NotFoundException("Item detail element not found");
            }
            String html = element.html();
            double value = CurrencyUtils.getCurrencySubstring(html);
            log.debug(String.format("Item detail html \"%s\" and value \"%f\"", html, value));
            return Optional.of(value);
        } catch (NotFoundException | IOException e) {
            log.debug(String.format("Item detail value element not found by selector \"%s\"", selector));
        }
        return Optional.empty();
    }

    private boolean hasDocumentBreakSelector(Element item, String selector) {
        Element element = item.selectFirst(selector);
        return element != null;
    }

    private Document loadContent(URL url) throws IOException {
        Connection connection = getConnection(url);
        Connection.Response response = connection.execute();
        int statusCode = response.statusCode();
        if (statusCode == HttpStatus.OK.value()) {
            return response.parse();
        }
        throw new HttpStatusException("Cannot load page", statusCode, url.toString());
    }

    private Connection getConnection(URL url) {
        return Jsoup
                .connect(url.toString())
                .header("Host", url.getHost())
                .header("User-Agent", appParserConnectionHeadersConfig.getUserAgent())
                .header("Accept", appParserConnectionHeadersConfig.getAccept())
                .header("Accept-Language", appParserConnectionHeadersConfig.getAcceptLanguage())
                .header("Accept-Encoding", appParserConnectionHeadersConfig.getAcceptEncoding())
                .header("DNT", String.valueOf(appParserConnectionHeadersConfig.getDnt()))
                .header("Connection", appParserConnectionHeadersConfig.getConnection())
                .header("Upgrade-Insecure-Requests", appParserConnectionHeadersConfig.getUpgradeInsecureRequests())
                .header("Pragma", appParserConnectionHeadersConfig.getPragma())
                .header("Cache-Control", appParserConnectionHeadersConfig.getCacheControl());
    }

    private Callable<Optional<ItemDetailParserResultDto>> createParserTask(Item item) {
        return () -> {
            try {
                Optional<ItemDetail> itemDetailOptional = parse(item);
                if (itemDetailOptional.isPresent()) {
                    return Optional.of(ItemDetailParserResultDtoMapper.toDto(item, itemDetailOptional.get(), true, null));
                }
            } catch (NotFoundException | IOException e) {
                return Optional.of(ItemDetailParserResultDtoMapper.toDto(item, null, false, e.getMessage()));
            }
            return Optional.empty();
        };
    }
}
