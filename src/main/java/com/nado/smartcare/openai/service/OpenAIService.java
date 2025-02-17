package com.nado.smartcare.openai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    private final ChatModel chatModel;
    private final ChatClient openAiChatClient;

    public OpenAIService(@Qualifier("openAiChatModel") ChatModel chatModel, @Qualifier("openAiChatClient") ChatClient openAiChatClient) {
        this.chatModel = chatModel;
        this.openAiChatClient = openAiChatClient;
    }


    public String generateNaturalLanguageExplanation(List<Map<String, Object>> results) {
        // SQL 쿼리 결과를 자연어 설명으로 변환하는 프롬프트 생성
        String prompt = "다음은 회원 이름 목록입니다. 이를 한국어로 간단하고 자연스럽게 설명해 주세요:\n"
                + results.toString()
                + "\n단, 결과는 쿼리나 기술적인 용어를 포함하지 말고, 사용자 친화적으로 작성해 주세요.";
        // OpenAI API 요청
/*        String request = chatClient.prompt(prompt)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getContent();

        return request;*/
        return "TEST";
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