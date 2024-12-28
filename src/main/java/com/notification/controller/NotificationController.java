package com.notification.controller;

import com.notification.NotificationBot;
import com.notification.model.NotificationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationBot notificationBot;

    public NotificationController(NotificationBot notificationBot) {
        this.notificationBot = notificationBot;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
        try {
            notificationBot.sendNotification(request.getHostEmail(), request.getMessage())
                .thenApply(response -> "Notification sent successfully");
            return ResponseEntity.ok("Notification queued for delivery");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to send notification: " + e.getMessage());
        }
    }
}
