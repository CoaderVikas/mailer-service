package com.mailer.dto;

import java.util.List;

import lombok.Data;

/**
 * Class      : VerifyEmailPayload
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jul 15, 2026
 * Version    : 1.0
 */
@Data
public class VerifyEmailPayload {
	private String from;
	private String[] to;
	private String subject;
	private String text;
	private List<String> cc;
	private List<String> bcc;
}
