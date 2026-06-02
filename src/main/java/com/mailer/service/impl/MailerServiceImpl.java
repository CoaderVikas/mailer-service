package com.mailer.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.mailer.dto.MailRequest;
import com.mailer.dto.MailResponse;
import com.mailer.service.MailService;

//import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * Mailer Service Implementation
 *
 * Features: - Async email sending - Retry mechanism - Circuit Breaker
 * protection - Thymeleaf HTML templates - CC / BCC support
 * 
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class MailerServiceImpl implements MailService {

	/**
	 * JavaMailSender for sending SMTP emails
	 */
	private final JavaMailSender mailSender;

	/**
	 * Thymeleaf template engine for HTML email templates
	 */
	private final SpringTemplateEngine templateEngine;

	/**
	 * Sender email from application.properties
	 */
	@Value("${spring.mail.username}")
	private String fromEmail;

	/**
	 * Send Mail Method
	 * @Async Runs method in background thread
	 * @Retryable Retry if MailException occurs
	 * @CircuitBreaker Stops calling SMTP when failures exceed threshold
	 */

	@Override
	@Async
	@Retryable(value = MailException.class, maxAttempts = 3, backoff = @Backoff(delay = 2000))
	// @CircuitBreaker(name = "mailService", fallbackMethod = "mailCircuitFallback")
	public CompletableFuture<MailResponse> sendMail(MailRequest request) {

		try {

			log.info("Preparing to send mail to {}", request.getTo());

			/**
			 * Step 1: Create MIME message
			 */
			MimeMessage message = mailSender.createMimeMessage();

			/**
			 * Step 2: MIME helper (supports HTML & attachments)
			 */
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			/**
			 * Step 3: Set basic email fields
			 */
			helper.setFrom(fromEmail);
			helper.setTo(request.getTo());
			helper.setSubject(request.getSubject());

			/**
			 * Step 4: Prepare template variables
			 */
			Map<String, Object> vars = new HashMap<>();

			vars.put("name", request.getToName());
			vars.put("message", request.getBody());
			vars.put("actionUrl", request.getActionUrl());
			vars.put("otp", request.getOtp());
			vars.put("invoiceNumber", request.getInvoiceNumber());
			vars.put("amount", request.getAmount());
			vars.put("invoiceUrl", request.getInvoiceUrl());

			/**
			 * Step 5: Generate HTML from Thymeleaf template
			 */
			String html = getHtmlFromTemplate(request.getTemplateName(), vars);

			/**
			 * Step 6: Set email HTML content
			 */
			helper.setText(html, true);

			/**
			 * Step 7: Optional CC
			 */
			if (request.getCc() != null && !request.getCc().isEmpty()) {
				helper.setCc(request.getCc().toArray(new String[0]));
			}

			/**
			 * Step 8: Optional BCC
			 */
			if (request.getBcc() != null && !request.getBcc().isEmpty()) {
				helper.setBcc(request.getBcc().toArray(new String[0]));
			}

			/**
			 * Step 9: Send email
			 */
			mailSender.send(message);

			log.info("Mail sent successfully to {}", request.getTo());

			MailResponse response = MailResponse.builder().success(true).message("Mail sent successfully").build();

			return CompletableFuture.completedFuture(response);

		} catch (MailException e) {

			/**
			 * MailException triggers retry
			 */
			log.error("MailException while sending mail to {}", request.getTo(), e);

			throw e;

		} catch (Exception e) {

			log.error("Unexpected error while sending mail to {}", request.getTo(), e);

			MailResponse response = MailResponse.builder().success(false).message("Mail failed: " + e.getMessage())
					.build();

			return CompletableFuture.completedFuture(response);
		}
	}

	/**
	 *Retry Fallback Method Called when all retry attempts fail
	 * 
	 */

	@Recover
	public CompletableFuture<MailResponse> recover(MailException ex, MailRequest request) {

		log.error("All retry attempts failed for {}", request.getTo(), ex);

		MailResponse response = MailResponse.builder().success(false)
				.message("Mail failed after retries: " + ex.getMessage()).build();

		return CompletableFuture.completedFuture(response);
	}

	/**
	 * Circuit Breaker Fallback Triggered when circuit breaker opens
	 */
	public CompletableFuture<MailResponse> mailCircuitFallback(MailRequest request, Throwable ex) {

		log.error("Circuit breaker triggered for {}", request.getTo(), ex);

		MailResponse response = MailResponse.builder().success(false).message("Mail service temporarily unavailable")
				.build();

		return CompletableFuture.completedFuture(response);
	}

	/**
	 *Generate HTML from Thymeleaf Template
	 */

	private String getHtmlFromTemplate(String templateName, Map<String, Object> variables) {

		Context context = new Context();
		context.setVariables(variables);

		return templateEngine.process("email/" + templateName, context);
	}

}