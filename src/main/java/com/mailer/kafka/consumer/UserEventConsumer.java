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

	@RetryableTopic(
	        attempts = "3",
	        // @Backoff(delay = 3000),
	        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
	        dltTopicSuffix = "-dlt"
	    )
	@KafkaListener(topics = "user-registration", groupId = "mailer-group")
	public void consume(UserRegisteredEvent event) {

		try {

			log.info("User registration event received for {}", event.getEmail());

			/*MailRequest request = MailRequest.builder()
					.to(event.getEmail())
					.toName(event.getFullName())
					.subject("Welcome to our platform")
					.body("Your account has been created successfully.")
					.templateName("welcome").build();*/
			MailRequest request = prepareWelcomeMail(event);
			

			mailService.sendMail(request);
			log.info("Mail request processed for {}", event.getEmail());

		} catch (Exception e) {

			log.error("Failed to process user registration event for {}", event.getEmail(), e);
		}
	}
	
	private MailRequest prepareWelcomeMail(UserRegisteredEvent user) {
		return MailRequest.builder().to(user.getEmail()).toName(user.getFullName()).subject("Welcome to Our App!")
				.templateName("welcome")
				.body("Hello " + user.getFullName() + ",\n\n"
						+ "Welcome to Our App! We're excited to have you on board. "
						+ "Get started by exploring our features and managing your profile.\n\n"
						+ "Happy journey,\nThe App Team")
				.actionUrl("https://yourapp.com/dashboard").build();
	}

}
