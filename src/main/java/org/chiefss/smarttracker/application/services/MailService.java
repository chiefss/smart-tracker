package org.chiefss.smarttracker.application.services;

import lombok.RequiredArgsConstructor;
import org.chiefss.smarttracker.application.configuration.AppMailConfig;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@RequiredArgsConstructor
public class MailService {

    private final AppMailConfig appMailConfig;

    public void sendAdmin(String subject, String body) {
        this.sendMail(subject, body, appMailConfig.getAdmin());
    }

    public void sendMail(String subject, String body, String to) {
        if (!appMailConfig.isEnabled()) {
            return;
        }

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(appMailConfig.getHost());
        mailSender.setPort(appMailConfig.getPropertiesSmtpPort());

        mailSender.setUsername(appMailConfig.getUsername());
        mailSender.setPassword(appMailConfig.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", appMailConfig.getPropertiesTransportProtocol());
        props.put("mail.smtp.auth", appMailConfig.isPropertiesSmtpAuth());
        props.put("mail.smtp.connectiontimeout", appMailConfig.getPropertiesSmtpConnectiontimeout());
        props.put("mail.smtp.timeout", appMailConfig.getPropertiesSmtpTimeout());
        props.put("mail.smtp.writetimeout", appMailConfig.getPropertiesSmtpWritetimeout());
        props.put("mail.smtp.starttls.enable", appMailConfig.isPropertiesSmtpStarttlsEnable());
        props.put("mail.smtp.starttls.required", appMailConfig.isPropertiesSmtpStarttlsRequired());
        props.put("mail.debug", appMailConfig.isDebug());

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(appMailConfig.getFrom());
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        simpleMailMessage.setTo(to);

        mailSender.send(simpleMailMessage);
    }
}
