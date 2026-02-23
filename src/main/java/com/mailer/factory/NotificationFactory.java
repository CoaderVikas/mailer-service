package com.mailer.factory;

import org.springframework.stereotype.Component;

import com.mailer.enums.NotificationType;
import com.mailer.service.NotificationService;
import com.mailer.service.impl.EmailNotificationService;

import lombok.RequiredArgsConstructor;

/**
 * Class      : NotificationFactory
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 22, 2026
 * Version    : 1.0
 */

@Component
@RequiredArgsConstructor
public class NotificationFactory {

    private final EmailNotificationService emailService;

    public NotificationService getService(NotificationType type) {

        switch (type) {
            case EMAIL:
                return emailService;
            default:
                throw new IllegalArgumentException("Unsupported type");
        }
    }
}