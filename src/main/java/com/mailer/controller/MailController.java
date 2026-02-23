package com.mailer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mailer.dto.MailRequest;
import com.mailer.dto.MailResponse;
import com.mailer.service.MailService;

import lombok.RequiredArgsConstructor;

/**
 * Class : MailController Description: [Add brief description here] Author :
 * Vikas Yadav Created On : Feb 22, 2026 Version : 1.0
 */

@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class MailController {

	private final MailService mailerService;

	@PostMapping("/send")
	public ResponseEntity<MailResponse> sendMail(@RequestBody MailRequest request) throws Exception{

		var response = mailerService.sendMail(request).get();

		if (response.isSuccess()) {
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}
