package com.mailer.listner;

import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.mailer.event.UserRegisteredEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * Class      : DeadLetterConsumer
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Mar 7, 2026
 * Version    : 1.0
 */

@Service
@Slf4j
@Lazy
public class DeadLetterConsumer {

	// GroupId ko alag rakha hai taaki main group se takkar na ho
    @KafkaListener(topics = "user-registration-dlt", groupId = "mailer-group-dlt-handler")
    public void consumeDLT(UserRegisteredEvent event) {
        
        log.error("XXXX MESSAGE PERMANENTLY FAILED: Event moved to DLT for email: {} XXXX", 
                  event.getEmail());

        // Tracking ke liye:
        // 1. Aap ise Database (failed_emails table) mein save kar sakte hain.
        // 2. Ya Slack/Email alert bhej sakte hain admin ko.
        saveToDatabase(event);
    }

    private void saveToDatabase(UserRegisteredEvent event) {
        log.info("Saving failed event to DB for manual review: {}", event.getEmail());
        // Repository logic yahan aayega
    }
}