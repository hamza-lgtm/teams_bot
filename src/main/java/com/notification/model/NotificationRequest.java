package com.notification.model;

import lombok.Data;

@Data
public class NotificationRequest {
    private String hostEmail;
    private String message;
}
