package com.mailer.service;

import com.mailer.dto.NotificationRequest;
import com.mailer.dto.NotificationResponse;

/**
 * Class      : MailerService
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 22, 2026
 * Version    : 1.0
 */

public interface NotificationService {
	
	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	NotificationResponse send(NotificationRequest request) throws Exception;
}
