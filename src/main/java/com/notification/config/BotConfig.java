package com.notification.config;

import com.microsoft.bot.builder.Bot;

import com.microsoft.bot.integration.BotFrameworkHttpAdapter;
import com.microsoft.bot.integration.Configuration;
import com.microsoft.bot.integration.spring.BotController;
import com.notification.NotificationBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@org.springframework.context.annotation.Configuration
@Import({BotController.class})
public class BotConfig {

    @Bean
    public BotFrameworkHttpAdapter getBotFrameworkHttpAdapter(Configuration configuration) {
        BotFrameworkHttpAdapter adapter = new BotFrameworkHttpAdapter(configuration);

        // Add error handling
        adapter.setOnTurnError((turnContext, exception) -> {
            // Log the exception here
            System.out.println("Exception caught in adapter: " + exception.getMessage());
            return turnContext.sendActivity("Sorry, something went wrong!").thenApply(resourceResponse -> null);
        });

        return adapter;
    }

    @Bean
    public Bot getBot(Configuration configuration) {
        return new NotificationBot(configuration);
    }
}
