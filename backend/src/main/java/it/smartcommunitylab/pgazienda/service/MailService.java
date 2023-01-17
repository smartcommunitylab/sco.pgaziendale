/*******************************************************************************
 * Copyright 2015 Fondazione Bruno Kessler
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 ******************************************************************************/
package it.smartcommunitylab.pgazienda.service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import it.smartcommunitylab.pgazienda.domain.User;


/**
 * Service for sending emails.
 * <p>
 * We use the {@link Async} annotation to send emails asynchronously.
 */
@Service
public class MailService {

    /**
	 * 
	 */
	private static final String LANG_KEY_IT = "it";

	private final Logger log = LoggerFactory.getLogger(MailService.class);

    private static final String USER = "user";

    private static final String BASE_URL = "baseUrl";

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${spring.mail.from}")
    private String mailFrom;
    @Value("${spring.mail.baseurl}")
    private String mailBaseUrl;

    
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart, isHtml, to, subject, content);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(mailFrom);
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        }  catch (MailException | MessagingException e) {
            log.warn("Email could not be sent to user '{}'", to, e);
        }
    }

    @Async
    public void sendEmailFromTemplate(User user, String langKey, String templateName, String titleKey) {
        if (user.getUsername() == null) {
            log.debug("Email doesn't exist for user '{}'", user.getUsername());
            return;
        }
        Locale locale = Locale.forLanguageTag(langKey);
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, mailBaseUrl);
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmail(user.getUsername(), subject, content, false, true);
    }

    @Async
    public void sendActivationEmail(User user) {
        log.debug("Sending activation email to '{}'", user.getUsername());
        sendEmailFromTemplate(user, LANG_KEY_IT, "mail/activationEmail", "email.activation.title");
    }

    @Async
    public void sendEmail(String template, String title, String receiver, Map<String, Object> vars) {
        try {
	        log.debug("Sending email to '{}'", receiver);
	        Locale locale = Locale.forLanguageTag(LANG_KEY_IT);
	        Context context = new Context(locale);
	        
	        if (vars != null) {
	        	vars.entrySet().forEach(e -> context.setVariable(e.getKey(), e.getValue()));
	        }
	        context.setVariable(BASE_URL, mailBaseUrl);
	        String content = templateEngine.process("mail/" + template, context);
	        String subject = title;
	        // Prepare message using a Spring helper
	        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, StandardCharsets.UTF_8.name());
            message.setTo(receiver);
            message.setFrom(mailFrom);
            message.setSubject(subject);
            message.setText(content, true);
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", receiver);
        }  catch (Exception e) {
            log.warn("Email could not be sent to user '{}'", receiver, e);
//            throw new RequestEmailFailureException();
        }
    	
    }

	@Async
    public void sendCreationEmail(User user) {
        log.debug("Sending creation email to '{}'", user.getUsername());
        sendEmailFromTemplate(user, LANG_KEY_IT, "mail/creationEmail", "email.activation.title");
    }

    @Async
    public void sendPasswordResetMail(User user) {
        log.debug("Sending password reset email to '{}'", user.getUsername());
        sendEmailFromTemplate(user, LANG_KEY_IT, "mail/passwordResetEmail", "email.reset.title");
    }

}