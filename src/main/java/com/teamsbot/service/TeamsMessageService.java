package com.teamsbot.service;

import com.microsoft.bot.builder.*;
import com.microsoft.bot.schema.*;
import com.teamsbot.model.TeamsUser;
import com.teamsbot.repository.TeamsUserRepository;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class TeamsMessageService {

    @Autowired
    private TeamsUserRepository teamsUserRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public CompletableFuture<Void> handleMessage(TurnContext turnContext) {
        String teamsUserId = turnContext.getActivity().getFrom().getId();
        Optional<TeamsUser> userOpt = teamsUserRepository.findByTeamsUserId(teamsUserId);

        if (userOpt.isEmpty()) {
            return sendWelcomeCard(turnContext);
        } else {
            TeamsUser user = userOpt.get();
            if (!user.isEmailVerified()) {
                return processEmailVerification(turnContext, user);
            } else {
                return handleVerifiedUserMessage(turnContext, user);
            }
        }
    }

    private CompletableFuture<Void> sendWelcomeCard(TurnContext turnContext) {
        Activity reply = MessageFactory.attachment(createWelcomeCard());
        TeamsUser newUser = new TeamsUser();
        newUser.setTeamsUserId(turnContext.getActivity().getFrom().getId());
        
        try {
            // Store the complete conversation reference as JSON
            ConversationReference conversationRef = turnContext.getActivity().getConversationReference();
            String conversationRefJson = objectMapper.writeValueAsString(conversationRef);
            System.out.println("Storing conversation reference: " + conversationRefJson);
            newUser.setConversationReference(conversationRefJson);
            teamsUserRepository.save(newUser);
        } catch (Exception e) {
            System.err.println("Error storing conversation reference: " + e.getMessage());
            e.printStackTrace();
        }
        
        return turnContext.sendActivity(reply).thenApply(resourceResponse -> null);
    }

    private Attachment createWelcomeCard() {
        HeroCard card = new HeroCard();
        card.setTitle("Welcome to the Teams Notification Bot!");
        card.setText("To get started, please provide your email address.");
        
        return card.toAttachment();
    }

    private CompletableFuture<Void> processEmailVerification(TurnContext turnContext, TeamsUser user) {
        String messageText = turnContext.getActivity().getText().trim();
        
        if (EmailValidator.getInstance().isValid(messageText)) {
            // Check if email is already in use
            Optional<TeamsUser> existingUser = teamsUserRepository.findByEmail(messageText);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
                return turnContext.sendActivity(MessageFactory.text("This email is already registered."))
                        .thenApply(resourceResponse -> null);
            }
            
            try {
                // Update conversation reference when email is verified
                ConversationReference conversationRef = turnContext.getActivity().getConversationReference();
                String conversationRefJson = objectMapper.writeValueAsString(conversationRef);
                System.out.println("Updating conversation reference: " + conversationRefJson);
                user.setConversationReference(conversationRefJson);
            } catch (Exception e) {
                System.err.println("Error updating conversation reference: " + e.getMessage());
                e.printStackTrace();
            }
            
            user.setEmail(messageText);
            user.setEmailVerified(true);
            teamsUserRepository.save(user);
            
            return turnContext.sendActivity(MessageFactory.text("Thank you! Your email has been verified. You can now use the bot."))
                    .thenApply(resourceResponse -> null);
        } else {
            return turnContext.sendActivity(MessageFactory.text("Please provide a valid email address."))
                    .thenApply(resourceResponse -> null);
        }
    }

    private CompletableFuture<Void> handleVerifiedUserMessage(TurnContext turnContext, TeamsUser user) {
        // String messageText = turnContext.getActivity().getText().trim();
        // Here you can implement command handling and notification processing
        return turnContext.sendActivity(MessageFactory.text("Your message has been received,Whene there is a visitor,we will notify you."))
                .thenApply(resourceResponse -> null);
    }
}
