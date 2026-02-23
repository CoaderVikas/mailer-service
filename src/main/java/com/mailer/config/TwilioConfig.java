package com.mailer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.twilio.Twilio;

import jakarta.annotation.PostConstruct;

/**
 * Class      : TwilioConfig
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 22, 2026
 * Version    : 1.0
 */

@Configuration
public class TwilioConfig {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String fromPhone;

    @PostConstruct
    public void init() {
        System.out.println("Twilio SID: " + accountSid);
        System.out.println("Twilio Token: " + authToken.substring(0,5) + "*****");
        Twilio.init(accountSid, authToken); // ✅ must call
    }

    public String getFromPhone() {
        return fromPhone;
    }
}
