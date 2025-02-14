package com.nado.smartcare.openai.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Log4j2
public class OllamaService {
    private final ChatModel chatModel;
    private final ChatClient ollamaChatClient;


    public OllamaService(@Qualifier("ollamaChatModel") ChatModel chatModel, ChatClient ollamaChatClient) {
        this.chatModel = chatModel;
        this.ollamaChatClient = ollamaChatClient;
    }

    public String getResponse(String message) {
        ChatResponse chatResponse = chatModel.call(
                new Prompt(
                        message,
                        OllamaOptions.builder()
                                .withModel(OllamaModel.MISTRAL)
                                .withTemperature(0.8)
                                .build()
                )
        );

        return chatResponse.getResult().getOutput().getContent();
    }

    public String getResponseWithPrompt(String promptTemplate, Map<String, Object> parameters) {
        PromptTemplate template = new PromptTemplate(promptTemplate);
        // Prompt 객체 생성
        Prompt basePrompt = template.create(parameters);

        // Ollama 특정 옵션을 포함한 새로운 Prompt 생성
        Prompt ollamaPrompt = new Prompt(
                basePrompt.getContents(),
                OllamaOptions.builder()
                        .withModel(OllamaModel.MISTRAL)
                        .withTemperature(0.8)
                        .build()
        );

        ChatResponse chatResponse = chatModel.call(ollamaPrompt);
        return chatResponse.getResult().getOutput().getContent();
    }

}