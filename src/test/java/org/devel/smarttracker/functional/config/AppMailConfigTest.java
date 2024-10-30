package org.devel.smarttracker.functional.config;

import org.devel.smarttracker.functional.AbstractFunctionalSpringBootTest;
import org.devel.smarttracker.application.configuration.AppMailConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;


@EnableConfigurationProperties(AppMailConfig.class)
@TestPropertySource(properties = {
        "app.mail.enabled=false",
        "app.mail.debug=false",
        "app.mail.admin=admin@example.com",
        "app.mail.from=no-reply@example.com",
        "app.mail.host=smtp.example.com",
        "app.mail.username=user",
        "app.mail.password=secret",
        "app.mail.properties.transport.protocol=smtp",
        "app.mail.properties.smtp.port=587",
        "app.mail.properties.smtp.connectiontimeout=5000",
        "app.mail.properties.smtp.timeout=5000",
        "app.mail.properties.smtp.writetimeout=5000",
        "app.mail.properties.smtp.auth=true",
        "app.mail.properties.smtp.starttls.enable=true",
        "app.mail.properties.smtp.starttls.required=true"
})
class AppMailConfigTest extends AbstractFunctionalSpringBootTest {

    @Autowired
    private AppMailConfig appMailConfig;

    @Test
    void testConfig() {
        Assertions.assertFalse(appMailConfig.isEnabled());
        Assertions.assertFalse(appMailConfig.isDebug());
        Assertions.assertNotNull(appMailConfig.getAdmin());
        Assertions.assertNotNull(appMailConfig.getFrom());
        Assertions.assertNotNull(appMailConfig.getHost());
        Assertions.assertNotNull(appMailConfig.getUsername());
        Assertions.assertNotNull(appMailConfig.getPassword());
        Assertions.assertNotNull(appMailConfig.getPropertiesTransportProtocol());
        Assertions.assertTrue(appMailConfig.getPropertiesSmtpPort() > 0);
        Assertions.assertTrue(appMailConfig.getPropertiesSmtpConnectiontimeout() > 0);
        Assertions.assertTrue(appMailConfig.getPropertiesSmtpTimeout() > 0);
        Assertions.assertTrue(appMailConfig.getPropertiesSmtpWritetimeout() > 0);
        Assertions.assertTrue(appMailConfig.isPropertiesSmtpAuth());
        Assertions.assertTrue(appMailConfig.isPropertiesSmtpStarttlsEnable());
        Assertions.assertTrue(appMailConfig.isPropertiesSmtpStarttlsRequired());
    }
}
