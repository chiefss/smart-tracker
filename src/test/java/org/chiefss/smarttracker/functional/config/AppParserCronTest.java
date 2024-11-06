package org.chiefss.smarttracker.functional.config;

import lombok.RequiredArgsConstructor;
import org.chiefss.smarttracker.functional.AbstractFunctionalSpringBootTest;
import org.chiefss.smarttracker.application.configuration.AppParserCronConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;


@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@EnableConfigurationProperties(AppParserCronConfig.class)
@TestPropertySource(properties = {
        "app.parser.cron.enabled=true",
        "app.parser.cron.max-thread=6",
        "app.parser.cron.initial-timeout=10",
        "app.parser.cron.interval=20"
})
class AppParserCronTest extends AbstractFunctionalSpringBootTest {

    private final AppParserCronConfig appParserCronConfig;

    @Test
    void testConfig() {
        Assertions.assertTrue(appParserCronConfig.isEnabled());
        Assertions.assertEquals(6, appParserCronConfig.getMaxThread());
        Assertions.assertEquals(10, appParserCronConfig.getInitialTimeout());
        Assertions.assertEquals(20, appParserCronConfig.getInterval());
    }
}
