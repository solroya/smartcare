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

//Aspect-Oriented Programming 활성화: AI관련 로직의 부가 기능(로깅, 트랜젝션관리)
/*
* 횡단 관심사: 로깅, 보안, 트랜잭션 관리와 같이 애플리케이션에서 반복적으로 사용되는 기능
* AOP를 통해 한번에 관리 가능 -> 횡단 관심사를 쉽게 분리 가능
* */
@EnableAspectJAutoProxy
@Configuration
public class AiConfig {

/*    @Bean
    public ChatClient openAiChatClient(ChatClient.Builder openAiChatClientBuilder) {
        return openAiChatClientBuilder.defaultAdvisors(
                new MessageChatMemoryAdvisor(new InMemoryChatMemory())
        ).build();
    }*/

    @Bean
    @Primary
    public ChatClient openAiChatClient(@Qualifier("openAiChatClientBuilder") ChatClient.Builder openAiChatClientBuilder) {
        return openAiChatClientBuilder.build();
// InMemory 기능 제거 버전
//    public ChatClient openAiChatClient(ChatClient.Builder openAiChatClientBuilder) {
//        return openAiChatClientBuilder.build();
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
