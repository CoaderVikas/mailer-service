package com.mailer.service.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.mailer.dto.MailRequest;
import com.mailer.dto.MailResponse;
import com.mailer.dto.VerifyEmailPayload;
import com.mailer.dto.VerifyEmailResponse;
import com.mailer.service.MailService;
import com.mailer.service.VerifyEmailService;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;

/**
 * VerifyEmailService
 *
 * Sends transactional emails (welcome, otp, invoice) using Resend API.
 *
 * Author: Vikas Yadav
 */
@Service
@Slf4j
public class VerifyEmailServiceImpl implements VerifyEmailService {

	@Value("${resend.api-key}")
	private String apiKey;

	@Value("${resend.sender-email}")
	private String senderEmail;

	private static final String RESEND_API_URL = "https://api.resend.com/emails";

	private final RestTemplate restTemplate = new RestTemplate();

	@Override
	@Async
	public CompletableFuture<MailResponse> sendMail(MailRequest request) throws MessagingException {

		MailResponse mailResponse = new MailResponse();

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(apiKey);

			VerifyEmailPayload payload = buildPayload(request);

			HttpEntity<VerifyEmailPayload> httpEntity = new HttpEntity<>(payload, headers);

			log.info("*********** Sending '{}' email via Resend to: {} ***********",
					request.getTemplateName(), request.getTo());

			ResponseEntity<VerifyEmailResponse> response = restTemplate.postForEntity(
					RESEND_API_URL, httpEntity, VerifyEmailResponse.class);

			if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
				mailResponse.setSuccess(true);
				mailResponse.setMessage(response.getBody() != null ? response.getBody().getId() : null);
				log.info("*********** Email sent successfully, id: {} ***********", mailResponse.getMessage());
			} else {
				mailResponse.setSuccess(false);
				mailResponse.setErrorMessage("Unexpected response: " + response.getStatusCode());
				log.error("*********** Email send failed with status: {} ***********", response.getStatusCode());
			}

		} catch (Exception e) {
			mailResponse.setSuccess(false);
			mailResponse.setErrorMessage(e.getMessage());
			log.error("*********** Failed to send email via Resend ***********", e);
		}

		return CompletableFuture.completedFuture(mailResponse);
	}

	/**
	 * Builds the Resend API payload from MailRequest, resolving the body
	 * based on templateName (welcome, otp, invoice) if a custom body isn't set.
	 */
	private VerifyEmailPayload buildPayload(MailRequest request) {

		VerifyEmailPayload payload = new VerifyEmailPayload();
		payload.setFrom(senderEmail);
		payload.setTo(new String[] { request.getTo() });
		payload.setSubject(request.getSubject());
		payload.setText(resolveBody(request));

		if (!CollectionUtils.isEmpty(request.getCc())) {
			payload.setCc(request.getCc());
		}
		if (!CollectionUtils.isEmpty(request.getBcc())) {
			payload.setBcc(request.getBcc());
		}

		return payload;
	}

	/**
	 * Resolves email body text based on templateName if body isn't explicitly provided.
	 */
	private String resolveBody(MailRequest request) {

		if (request.getBody() != null && !request.getBody().isBlank()) {
			return request.getBody();
		}

		String name = request.getToName() != null ? request.getToName() : "User";

		if (request.getTemplateName() == null) {
			return "";
		}

		switch (request.getTemplateName().toLowerCase()) {

			case "otp":
				return "Hi " + name + ",\n\nYour OTP is: " + request.getOtp()
						+ "\nThis code will expire in 5 minutes.\n\nRentSafe,\nTeam";

			case "welcome":
				return "Hi " + name + ",\n\nWelcome aboard! We're glad to have you.\n"
						+ (request.getActionUrl() != null ? "Get started here: " + request.getActionUrl() : "")
						+ "\n\nRegards,\nTeam";

			case "invoice":
				return "Hi " + name + ",\n\nYour invoice " + request.getInvoiceNumber()
						+ " of amount " + request.getAmount() + " is ready.\n"
						+ (request.getInvoiceUrl() != null ? "View invoice: " + request.getInvoiceUrl() : "")
						+ "\n\nRegards,\nTeam";

			default:
				return "";
		}
	}
}