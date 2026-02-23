package com.mailer.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Class      : NotificationResponse
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 22, 2026
 * Version    : 1.0
 */

@Data
@Builder
public class NotificationResponse {

    /**
     * Indicates whether the notification was sent successfully
     */
    private boolean success;

    /**
     * Message describing the result (success/failure details)
     */
    private String message;
}