package com.mailer.service;

import java.util.concurrent.CompletableFuture;

import com.mailer.dto.MailRequest;
import com.mailer.dto.MailResponse;

import jakarta.mail.MessagingException;

/**
 * Class      : VerifyEmailService
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jul 15, 2026
 * Version    : 1.0
 */

public interface VerifyEmailService{
	public CompletableFuture<MailResponse> sendMail(MailRequest request) throws MessagingException;
}
