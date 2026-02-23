package com.mailer.service.impl;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.springframework.resilience.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.mailer.config.TwilioConfig;
import com.mailer.dto.SmsRequest;
import com.mailer.dto.SmsResponse;
import com.mailer.service.SmsService;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Class      : SmsServiceImpl
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 22, 2026
 * Version    : 1.0
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

	private final TwilioConfig twilioConfig;

    @Override
    @Async
    @Retryable(value = Exception.class, maxRetries = 3, delay = 2000)
    public CompletableFuture<SmsResponse> sendOtp(SmsRequest request) {

        try {
            // 1️ Generate OTP if not provided
            if (request.getOtp() == null || request.getOtp().isEmpty()) {
                request.setOtp(generateOtp(request.getOtpLength() > 0 ? request.getOtpLength() : 6));
            }

            String smsMessage = "Your OTP is: " + request.getOtp();

            log.info("Sending SMS via Twilio to {}: {}", request.getMobileNumber(), smsMessage);

            // 2️⃣ Send SMS using Twilio
            Message twilioMessage = Message.creator(
                    new PhoneNumber(request.getMobileNumber()),
                    new PhoneNumber(twilioConfig.getFromPhone()),
                    smsMessage
            ).create();

            log.info("Twilio message SID: " + twilioMessage.getSid());

            return CompletableFuture.completedFuture(
                    SmsResponse.builder()
                            .success(true)
                            .message("OTP sent successfully to " + request.getMobileNumber())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error sending SMS to {}: {}", request.getMobileNumber(), e.getMessage(), e);
            throw e; // trigger retry
        }
    }
	
	// Generate numeric OTP
	private String generateOtp(int length) {
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(random.nextInt(10)); // 0-9
		}
		return sb.toString();
	}

}
