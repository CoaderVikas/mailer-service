package com.mailer.service.impl;

import org.springframework.stereotype.Service;

import com.mailer.dto.MailRequest;
import com.mailer.dto.NotificationRequest;
import com.mailer.dto.NotificationResponse;
import com.mailer.service.MailService;
import com.mailer.service.NotificationService;

import lombok.RequiredArgsConstructor;

/**
 * Class : EmailNotificationService 
 * Description: [Add brief description here]
 * Author : Vikas Yadav 
 * Created On : Feb 22, 2026 Version : 1.0
 */

@Service
@RequiredArgsConstructor
public class EmailNotificationService implements NotificationService {

	private final MailService mailerService;

	@Override
	public NotificationResponse send(NotificationRequest request) throws Exception {

		MailRequest mailRequest = MailRequest.builder()
											.to(request.getTo())
											.subject(request.getSubject())
											.body(request.getBody())
											.build();

		var response = mailerService.sendMail(mailRequest).get();

		return NotificationResponse.builder().success(response.isSuccess()).message(response.getMessage()).build();
	}
}
