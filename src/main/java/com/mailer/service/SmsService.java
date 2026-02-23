package com.mailer.service;

import java.util.concurrent.CompletableFuture;

import com.mailer.dto.SmsRequest;
import com.mailer.dto.SmsResponse;

/**
 * Class      : SmsService
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 22, 2026
 * Version    : 1.0
 */

public interface SmsService {
	/**
	 * 
	 * @param request
	 * @return
	 */
	CompletableFuture<SmsResponse> sendOtp(SmsRequest request);
}
