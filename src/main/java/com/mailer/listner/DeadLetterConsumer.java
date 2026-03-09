package com.mailer.listner;

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
public class DeadLetterConsumer {

    @KafkaListener(topics = "user-registration-dlt", groupId = "mailer-group")
    public void consumeDLT(UserRegisteredEvent event) {

        log.error("Event moved to DLT for email {}", event.getEmail());

        // yaha future me:
        // DB store
        // alert
        // manual retry
    }
}
