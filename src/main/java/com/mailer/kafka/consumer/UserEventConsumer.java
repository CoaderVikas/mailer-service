package com.mailer.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import com.mailer.dto.MailRequest;
import com.mailer.event.UserRegisteredEvent;
import com.mailer.service.MailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Class      : UserEventConsumer
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Mar 7, 2026
 * Version    : 1.0
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

	private final MailService mailService;

	@RetryableTopic(attempts = "3", 										// Total 3 baar try karega (1 original + 2 retries)
			// backOff = @Backoff(delay = 3000, multiplier = 2.0), 			// Har baar double gap // lega (3s, 6s)
			topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE, dltTopicSuffix = "-dlt", include = {Exception.class } // Har tarah ki exception par retry karega
	)
	@KafkaListener(topics = "user-registration", groupId = "mailer-group")
	public void consume(UserRegisteredEvent event) throws Exception {
		try {
			log.info(">>>> Consuming event for email: {}", event.getEmail());

			MailRequest request = prepareWelcomeMail(event);
			mailService.sendMail(request);

			log.info("<<<< Successfully processed mail for: {}", event.getEmail());

		} catch (Exception e) {
			// Log error with full stack trace for debugging
			log.error("#### ERROR in MailerService for email: {}. Reason: {}", event.getEmail(), e.getMessage());

			// CRITICAL: Exception ko throw karna zaroori hai
			// taaki @RetryableTopic ise pakad sake aur Retry/DLT logic chalaye.
			throw e;
		}
	}

	private MailRequest prepareWelcomeMail(UserRegisteredEvent user) {
		return MailRequest.builder().to(user.getEmail()).toName(user.getFullName()).subject("Welcome to Our App!")
				.templateName("welcome").body("Hello " + user.getFullName() + ",\n\nWelcome on board!")
				.actionUrl("https://yourapp.com/dashboard").build();
	}
}
