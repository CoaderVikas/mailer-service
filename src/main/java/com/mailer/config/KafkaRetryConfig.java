package com.mailer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaRetryTopic;

/**
 * Class      : KafkaRetryConfig
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Mar 7, 2026
 * Version    : 1.0
 */

@Configuration
@EnableKafka
@EnableKafkaRetryTopic
public class KafkaRetryConfig {

}
