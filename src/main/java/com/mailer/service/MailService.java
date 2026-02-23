package com.mailer.service;

import java.util.concurrent.CompletableFuture;

import com.mailer.dto.MailRequest;
import com.mailer.dto.MailResponse;

import jakarta.mail.MessagingException;

/**
 * Class      : MailService
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 22, 2026
 * Version    : 1.0
 */

public interface MailService {

	/**
	 * 
	 * @param request
	 * @return
	 * @throws MessagingException 
	 */
	public CompletableFuture<MailResponse> sendMail(MailRequest request) throws MessagingException;
}
