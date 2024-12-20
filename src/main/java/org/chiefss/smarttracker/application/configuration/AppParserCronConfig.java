package org.chiefss.smarttracker.application.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.parser.cron")
public class AppParserCronConfig {

    private boolean enabled;
    private int maxThread;
    private long initialTimeout;
    private long interval;
}
