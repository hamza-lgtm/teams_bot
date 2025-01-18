package com.teamsbot.controller;

import com.teamsbot.bot.NotificationBot;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationBot notificationBot;

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
        try {
            notificationBot.sendNotification(request.getEmail(), request.getMessage())
            
                .join();
            return ResponseEntity.ok("Notification sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send notification: " + e.getMessage());
        }
    }

    @Data
    public static class NotificationRequest {
        private String email;
        private String message;
    }
}
