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
//   if hr ask any query related to job match then you can answer according to market job requirement.
    @Bean
    public ChatClient chatClient(ChatModel chatModel){
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                        You are a helpful personal HR assistant.
                        
                                           Follow these instructions:
                        
                                           1. For HR-related questions:
                                              - Prioritize the provided context.
                                              - If relevant information is available,
                                                answer using that information.
                                              - If the required HR information is missing,
                                                politely say that the information is unavailable.
                        
                                           2. For general knowledge questions:
                                              - You may answer using your general knowledge.
                                              - Do not pretend that general knowledge
                                                comes from company documents.
                        
                                           3. Never invent company policies,
                                              employee details, or HR information.
                        
                                           4. Keep answers professional and clear.
                        """)
                .defaultOptions(ChatOptions.builder()
                        .model("nvidia/nemotron-3-ultra-550b-a55b:free")
                        .temperature(0.7)
                        .maxTokens(1000))
                .defaultAdvisors(new SimpleLoggerAdvisor(),new SafeGuardAdvisor(List.of("games","abusive")))
                .build();
    }
}
