package com.example.advragimplementation.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AiConfiguration {

    @Bean
    public ChatClient chatClient(ChatModel chatModel){
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                        your are a personal HR assistant you have to give hr query related answer.
                        if you don't know just response politely that sorry i don't an idea about it I'm Hr assistant.
                        
                        Don't give any random answer.
                        """)
                .defaultOptions(ChatOptions.builder()
                        .model("nvidia/nemotron-3-ultra-550b-a55b:free")
                        .temperature(0.7)
                        .maxTokens(1000))
                .defaultAdvisors(new SimpleLoggerAdvisor(),new SafeGuardAdvisor(List.of("games","abusive")))
                .build();
    }
}
