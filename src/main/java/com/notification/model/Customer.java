package com.notification.model;

import lombok.Data;

@Data
public class Customer {
    private String id;
    private String email;
    private String organizationName;
    private String teamsUserId;  // Teams user ID after bot installation
    private boolean isActive;
}
