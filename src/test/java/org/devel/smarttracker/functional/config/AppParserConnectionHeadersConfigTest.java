package org.devel.smarttracker.functional.config;

import org.devel.smarttracker.functional.AbstractFunctionalSpringBootTest;
import org.devel.smarttracker.application.configuration.AppParserConnectionHeadersConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;

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

    @Autowired
    private AppParserConnectionHeadersConfig appParserConnectionHeadersConfig;

    @Test
    void testConfig() {
        Assertions.assertNotNull(appParserConnectionHeadersConfig.getUserAgent());
        Assertions.assertNotNull(appParserConnectionHeadersConfig.getAccept());
        Assertions.assertNotNull(appParserConnectionHeadersConfig.getAcceptLanguage());
        Assertions.assertNotNull(appParserConnectionHeadersConfig.getAcceptLanguage());
        Assertions.assertNotNull(appParserConnectionHeadersConfig.getDnt());
        Assertions.assertNotNull(appParserConnectionHeadersConfig.getConnection());
        Assertions.assertNotNull(appParserConnectionHeadersConfig.getUpgradeInsecureRequests());
        Assertions.assertNotNull(appParserConnectionHeadersConfig.getPragma());
        Assertions.assertNotNull(appParserConnectionHeadersConfig.getCacheControl());
    }
}
