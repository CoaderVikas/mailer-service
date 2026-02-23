package com.mailer.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mailer.dto.SmsRequest;
import com.mailer.dto.SmsResponse;
import com.mailer.service.SmsService;

import lombok.RequiredArgsConstructor;

/**
 * Class      : SmsController
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 22, 2026
 * Version    : 1.0
 */

@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/send-otp")
    public ResponseEntity<SmsResponse> sendOtp(@RequestBody SmsRequest request) {
        CompletableFuture<SmsResponse> response = smsService.sendOtp(request);
        return ResponseEntity.ok(response.join());
    }
}
