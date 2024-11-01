package org.devel.smarttracker.functional.config;

import lombok.RequiredArgsConstructor;
import org.devel.smarttracker.functional.AbstractFunctionalSpringBootTest;
import org.devel.smarttracker.application.configuration.AppParserConnectionHeadersConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@EnableConfigurationProperties(AppParserConnectionHeadersConfig.class)
@TestPropertySource(properties = {
        "app.parser.connection.headers.userAgent=Mozilla/5.0",
        "app.parser.connection.headers.accept=text/html",
        "app.parser.connection.headers.acceptLanguage=en-US",
        "app.parser.connection.headers.acceptEncoding=gzip, deflate",
        "app.parser.connection.headers.dnt=1",
        "app.parser.connection.headers.connection=keep-alive",
        "app.parser.connection.headers.upgradeInsecureRequests=1",
        "app.parser.connection.headers.pragma=no-cache",
        "app.parser.connection.headers.cacheControl=no-cache"
})
class AppParserConnectionHeadersConfigTest extends AbstractFunctionalSpringBootTest {

    private final AppParserConnectionHeadersConfig appParserConnectionHeadersConfig;

    @Test
    void testConfig() {
        Assertions.assertEquals("Mozilla/5.0", appParserConnectionHeadersConfig.getUserAgent());
        Assertions.assertEquals("text/html", appParserConnectionHeadersConfig.getAccept());
        Assertions.assertEquals("en-US", appParserConnectionHeadersConfig.getAcceptLanguage());
        Assertions.assertEquals("gzip, deflate", appParserConnectionHeadersConfig.getAcceptEncoding());
        Assertions.assertEquals(1, appParserConnectionHeadersConfig.getDnt());
        Assertions.assertEquals("keep-alive", appParserConnectionHeadersConfig.getConnection());
        Assertions.assertEquals("1", appParserConnectionHeadersConfig.getUpgradeInsecureRequests());
        Assertions.assertEquals("no-cache", appParserConnectionHeadersConfig.getPragma());
        Assertions.assertEquals("no-cache", appParserConnectionHeadersConfig.getCacheControl());
    }
}
