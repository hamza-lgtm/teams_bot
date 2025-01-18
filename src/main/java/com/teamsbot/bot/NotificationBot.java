package com.teamsbot.bot;

import com.microsoft.bot.builder.*;
import com.microsoft.bot.schema.*;
import com.teamsbot.model.TeamsUser;
import com.teamsbot.repository.TeamsUserRepository;
import com.teamsbot.service.TeamsMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.microsoft.bot.integration.BotFrameworkHttpAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class NotificationBot extends ActivityHandler {
    @Autowired
    private TeamsUserRepository teamsUserRepository;

    @Autowired
    private TeamsMessageService teamsMessageService;

    @Autowired
    private BotFrameworkHttpAdapter adapter;

    @Value("${MicrosoftAppId}")
    private String botAppId;

    private final ObjectMapper objectMapper;

    public NotificationBot() {
        this.objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {
        return teamsMessageService.handleMessage(turnContext);
    }

    @Override
    protected CompletableFuture<Void> onConversationUpdateActivity(TurnContext turnContext) {
        if (turnContext.getActivity().getMembersAdded() != null) {
            return onMembersAdded(turnContext.getActivity().getMembersAdded(), turnContext);
        }
        return CompletableFuture.completedFuture(null);
    }

    protected CompletableFuture<Void> onMembersAdded(List<ChannelAccount> membersAdded, TurnContext turnContext) {
        return CompletableFuture.allOf(
            membersAdded.stream()
                .filter(member -> !member.getId().equals(turnContext.getActivity().getRecipient().getId()))
                .map(member -> teamsMessageService.handleMessage(turnContext))
                .toArray(CompletableFuture[]::new)
        );
    }

    public CompletableFuture<ResourceResponse> sendNotification(String email, String message) {
        
        TeamsUser user = teamsUserRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        try {
            Activity proactiveMessage = MessageFactory.text(message);
            
            // Get the stored conversation reference
            String conversationRefJson = user.getConversationReference();
            System.out.println("Stored conversation reference: " + conversationRefJson);
            
            if (conversationRefJson == null || conversationRefJson.trim().isEmpty()) {
                throw new RuntimeException("No conversation reference found for user");
            }

            ConversationReference conversationRef = objectMapper.readValue(
                conversationRefJson, 
                ConversationReference.class
            );
            
            CompletableFuture<ResourceResponse> responseFuture = new CompletableFuture<>();

            adapter.continueConversation(
                botAppId,
                conversationRef,
                (turnContext) -> turnContext.sendActivity(proactiveMessage)
                    .thenAccept(responseFuture::complete)
                    .exceptionally(ex -> {
                        responseFuture.completeExceptionally(ex);
                        return null;
                    }));

            return responseFuture;
        } catch (Exception e) {
            System.err.println("Error sending notification: " + e.getMessage());
            e.printStackTrace();
            CompletableFuture<ResourceResponse> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(
                new RuntimeException("Failed to send notification: " + e.getMessage()));
            return failedFuture;
        }
    }
}
