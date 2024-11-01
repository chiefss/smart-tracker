package org.devel.smarttracker.functional.config;

import lombok.RequiredArgsConstructor;
import org.devel.smarttracker.functional.AbstractFunctionalSpringBootTest;
import org.devel.smarttracker.application.configuration.AppMailConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;


@RequiredArgsConstructor(onConstructor = @__(@Autowired))
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

    private final AppMailConfig appMailConfig;

    @Test
    void testConfig() {
        Assertions.assertFalse(appMailConfig.isEnabled());
        Assertions.assertFalse(appMailConfig.isDebug());
        Assertions.assertEquals("admin@example.com", appMailConfig.getAdmin());
        Assertions.assertEquals("no-reply@example.com", appMailConfig.getFrom());
        Assertions.assertEquals("smtp.example.com", appMailConfig.getHost());
        Assertions.assertEquals("user", appMailConfig.getUsername());
        Assertions.assertEquals("secret", appMailConfig.getPassword());
        Assertions.assertEquals("smtp", appMailConfig.getPropertiesTransportProtocol());
        Assertions.assertEquals(587, appMailConfig.getPropertiesSmtpPort());
        Assertions.assertEquals(5000, appMailConfig.getPropertiesSmtpConnectiontimeout());
        Assertions.assertEquals(5000, appMailConfig.getPropertiesSmtpTimeout());
        Assertions.assertEquals(5000, appMailConfig.getPropertiesSmtpWritetimeout());
        Assertions.assertTrue(appMailConfig.isPropertiesSmtpAuth());
        Assertions.assertTrue(appMailConfig.isPropertiesSmtpStarttlsEnable());
        Assertions.assertTrue(appMailConfig.isPropertiesSmtpStarttlsRequired());
    }
}
