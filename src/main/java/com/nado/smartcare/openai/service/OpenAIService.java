package com.nado.smartcare.openai.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OpenAIService {

    private final ChatModel chatModel;

    public OpenAIService(@Qualifier("openAiChatModel") ChatModel chatModel) {
    //public OpenAIService(@Qualifier("openAiChatModel") ChatModel chatModel) {

        this.chatModel = chatModel;
    }

    public String getResponse(String message) {
        return chatModel.call(message);
    }

    public String getResponseWithPrompt(String promptTemplate, Map<String, Object> parameters) {
        PromptTemplate template = new PromptTemplate(promptTemplate);
        // create() 메서드는 Prompt 객체를 반환
        Prompt prompt = template.create(parameters);

        // ChatModel은 Prompt 객체를 직접 처리
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getContent();
    }

}