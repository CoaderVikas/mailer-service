package com.mailer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class : SmsRequest 
 * Description: [Add brief description here] 
 * Author : Vikas Yadav 
 * Created On : Feb 22, 2026 
 * Version : 1.0
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsRequest {
	private String mobileNumber; // recipient mobile
	private String message; // message body
	private String otp; // OTP (auto generated)
	private int otpLength;
}
