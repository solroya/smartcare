package com.nado.smartcare.openai.config;

import org.springframework.ai.autoconfigure.chat.client.ChatClientBuilderConfigurer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;

@EnableAspectJAutoProxy
@Configuration
public class AiConfig {

/*    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder.defaultAdvisors(
                new MessageChatMemoryAdvisor(new InMemoryChatMemory())
        ).build();
    }*/

    @Bean
    @Primary
    public ChatClient openAiChatClient(@Qualifier("openAiChatClientBuilder") ChatClient.Builder openAiChatClientBuilder) {
        return openAiChatClientBuilder.defaultAdvisors(
                new MessageChatMemoryAdvisor(new InMemoryChatMemory())
        ).build();
    }

    @Bean
    public ChatClient ollamaChatClient(@Qualifier("ollamaChatClientBuilder") ChatClient.Builder ollamaChatClientBuilder) {
        return ollamaChatClientBuilder.defaultAdvisors(
                new MessageChatMemoryAdvisor(new InMemoryChatMemory())
        ).build();
    }

    @Bean
    ChatClient.Builder openAiChatClientBuilder(ChatClientBuilderConfigurer chatClientBuilderConfigurer,
                                               @Qualifier("openAiChatModel") ChatModel chatModel) {
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        return chatClientBuilderConfigurer.configure(builder);
    }

    @Bean
    ChatClient.Builder ollamaChatClientBuilder(ChatClientBuilderConfigurer chatClientBuilderConfigurer,
                                               @Qualifier("ollamaChatModel") ChatModel chatModel) {
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        return chatClientBuilderConfigurer.configure(builder);
    }

}
