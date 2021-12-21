package org.devel.smarttracker.application.parsers;

import javassist.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.devel.smarttracker.application.configuration.AppParserConnectionHeadersConfig;
import org.devel.smarttracker.application.dto.ItemDetailParserResultDto;
import org.devel.smarttracker.application.entities.Item;
import org.devel.smarttracker.application.entities.ItemDetail;
import org.devel.smarttracker.application.services.ItemDetailService;
import org.devel.smarttracker.application.services.ItemService;
import org.devel.smarttracker.application.utils.Defines;
import org.devel.smarttracker.application.utils.CurrencyUtils;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;


@Log4j2
@Component
public class ItemDetailParser {

    @Autowired
    private AppParserConnectionHeadersConfig appParserConnectionHeadersConfig;

    public static final int MAX_THREADS = 8;

    public static final String SELECTOR_SPLIT_DELIMITER = "\\|";

    private final ItemService itemService;

    private final ItemDetailService itemDetailService;

    public ItemDetailParser(ItemService itemService, ItemDetailService itemDetailService) {
        this.itemService = itemService;
        this.itemDetailService = itemDetailService;
    }

    public List<ItemDetailParserResultDto> parseAll() {
        List<Item> items = itemService.findAll(true);
        List<ItemDetailParserResultDto> itemDetailParserResultDtos = new ArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);
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
            ItemDetail itemDetail = buildItemDetail(item, itemDetailValue);
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

    private ItemDetail buildItemDetail(Item item, Double value) {
        ItemDetail itemDetail = new ItemDetail();
        itemDetail.setItem(item);
        itemDetail.setValue(value);
        return itemDetail;
    }

    private boolean isActualValue(ItemDetail itemDetail) {
        List<ItemDetail> itemDetailList = itemDetailService.findLast(itemDetail.getItem());
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
        if (statusCode == 200) {
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
                .header("DNT", appParserConnectionHeadersConfig.getDnt())
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
                    return Optional.of(new ItemDetailParserResultDto(item, itemDetailOptional.get(), true, null));
                }
            } catch (NotFoundException | IOException e) {
                return Optional.of(new ItemDetailParserResultDto(item, null, false, e.getMessage()));
            }
            return Optional.empty();
        };
    }
}
