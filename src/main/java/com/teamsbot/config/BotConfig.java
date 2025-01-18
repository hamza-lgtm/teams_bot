package com.teamsbot.config;

import com.microsoft.bot.builder.Bot;

import com.microsoft.bot.integration.spring.BotController;
import com.microsoft.bot.integration.spring.BotDependencyConfiguration;
import com.teamsbot.bot.NotificationBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@org.springframework.context.annotation.Configuration
@Import({BotController.class})
public class BotConfig extends BotDependencyConfiguration {
    @Bean
    @Primary
    public Bot getBot() {
        return new NotificationBot();
    }
}
