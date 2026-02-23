package com.mailer.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.retry.annotation.Recover;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.mailer.dto.MailRequest;
import com.mailer.dto.MailResponse;
import com.mailer.service.MailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Class : MailerServiceImpl Description: [Add brief description here] Author :
 * Vikas Yadav Created On : Feb 22, 2026 Version : 1.0
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class MailerServiceImpl implements MailService {

	private final JavaMailSender mailSender;
	private final SpringTemplateEngine templateEngine;

	@Value("${spring.mail.username}")
	private String fromEmail;

	MimeMessageHelper helper;
	/**
	 * Send an email asynchronously
	 * 
	 * @param request MailRequest object containing to, subject, body, cc, bcc
	 * @return MailResponse indicating success or failure
	 * @throws MessagingException
	 */
	@Override
	@Async // run asynchronously
	@Retryable( 
	    value = MailException.class, // retry on MailException
	    maxRetries = 3,             // maximum 3 attempts
	    delay = 2000                 // 2 seconds fixed delay between retries
	)
	public CompletableFuture<MailResponse> sendMail(MailRequest request) {

	    try {
	        log.info("Preparing to send mail to {}", request.getTo());

	        // 1️⃣ Create MIME message
	        MimeMessage message = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true); // true = multipart (HTML)

	        // 2️⃣ Set From, To, Subject
	        helper.setFrom(fromEmail);
	        helper.setTo(request.getTo());
	        helper.setSubject(request.getSubject());

	        // 3️⃣ Prepare Thymeleaf context variables
	        Map<String, Object> vars = new HashMap<>();
	        vars.put("name", request.getToName());
	        vars.put("message", request.getBody());
	        vars.put("actionUrl", request.getActionUrl());
	        vars.put("otp", request.getOtp());
	        vars.put("invoiceNumber", request.getInvoiceNumber());
	        vars.put("amount", request.getAmount());
	        vars.put("invoiceUrl", request.getInvoiceUrl());

	        // 4️⃣ Generate HTML from template
	        //    templateName could be "welcome", "otp", "invoice" based on request
	        String html = getHtmlFromTemplate(request.getTemplateName(), vars);

	        // 5️⃣ Set HTML content in email
	        helper.setText(html, true); // true = HTML content

	        // 6️⃣ Optional CC
	        if (request.getCc() != null && !request.getCc().isEmpty()) {
	            helper.setCc(request.getCc().toArray(new String[0]));
	        }

	        // 7️⃣ Optional BCC
	        if (request.getBcc() != null && !request.getBcc().isEmpty()) {
	            helper.setBcc(request.getBcc().toArray(new String[0]));
	        }

	        // 8️⃣ Send email
	        mailSender.send(message);
	        log.info("Mail sent successfully to {}", request.getTo());

	        // 9️⃣ Return successful response
	        return CompletableFuture.completedFuture(
	            MailResponse.builder()
	                .success(true)
	                .message("Mail sent successfully")
	                .build()
	        );

	    } catch (MailException e) {
	        // 10️⃣ Retry will trigger automatically because of @Retryable
	        log.error("MailException occurred while sending mail to {}: {}", request.getTo(), e.getMessage());
	        throw e; // Important: must throw to trigger retry

	    } catch (Exception e) {
	        // 11️⃣ Any other exception
	        log.error("Unexpected error while sending mail to {}: {}", request.getTo(), e.getMessage(), e);
	        return CompletableFuture.completedFuture(
	            MailResponse.builder()
	                .success(false)
	                .message("Failed to send mail: " + e.getMessage())
	                .build()
	        );
	    }
	}

	
	@Recover
	public CompletableFuture<MailResponse> recover(MailException ex, MailRequest request) {

	    log.error("All retry attempts failed for {}", request.getTo(), ex);

	    return CompletableFuture.completedFuture(
	            MailResponse.builder()
	                    .success(false)
	                    .message("Mail failed after 3 attempts: " + ex.getMessage())
	                    .build()
	    );
	}
	
	private String getHtmlFromTemplate(String templateName, Map<String, Object> variables) {
	    Context context = new Context();
	    context.setVariables(variables);
	    return templateEngine.process("email/" + templateName, context); // template path: src/main/resources/templates/email/
	}

}
