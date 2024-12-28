package com.notification;

import com.microsoft.bot.builder.*;
import com.microsoft.bot.integration.Configuration;
import com.microsoft.bot.schema.*;
import com.notification.model.Customer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class NotificationBot extends ActivityHandler {
    private final Configuration configuration;
    private final Map<String, Customer> registeredCustomers;

    public NotificationBot(Configuration configuration) {
        this.configuration = configuration;
        this.registeredCustomers = new HashMap<>();
    }

    @Override
    protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {
        Activity activity = turnContext.getActivity();
        String userMessage = activity.getText().toLowerCase();
        
        if (userMessage.equals("register")) {
            Customer customer = new Customer();
            customer.setTeamsUserId(activity.getFrom().getId());
            customer.setEmail(activity.getFrom().getName() + "@" + activity.getChannelId());
            customer.setActive(true);
            
            registeredCustomers.put(customer.getEmail(), customer);
            
            return turnContext.sendActivity(MessageFactory.text("You have been registered successfully! You will now receive notifications."))
                    .thenApply(resourceResponse -> null);
        }
        
        return turnContext.sendActivity(MessageFactory.text("Hello! Type 'register' to start receiving notifications."))
                .thenApply(resourceResponse -> null);
    }

    public CompletableFuture<ResourceResponse> sendNotification(String userEmail, String message) {
        Customer customer = registeredCustomers.get(userEmail);
        if (customer == null || !customer.isActive()) {
            CompletableFuture<ResourceResponse> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("User not found or not active: " + userEmail));
            return future;
        }

        Activity activity = MessageFactory.text(message);
        activity.setRecipient(new ChannelAccount(customer.getTeamsUserId()));

        // In production, you would:
        // 1. Store conversation references when users interact with the bot
        // 2. Use BotAdapter.continueConversation to send proactive messages
        // 3. Implement proper error handling and retries

        // For now, return a dummy response
        ResourceResponse response = new ResourceResponse();
        response.setId("dummy-id");
        return CompletableFuture.completedFuture(response);
    }

    @Override
    protected CompletableFuture<Void> onMembersAdded(List<ChannelAccount> membersAdded, TurnContext turnContext) {
        return CompletableFuture.allOf(
            membersAdded.stream()
                .filter(member -> !member.getId().equals(turnContext.getActivity().getRecipient().getId()))
                .map(channel -> turnContext.sendActivity(MessageFactory.text(
                    "Welcome! I'm your notification bot. Type 'register' to start receiving notifications.")))
                .toArray(CompletableFuture[]::new)
        );
    }
}
