package com.nado.smartcare.openai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    private final ChatClient chatClient;

    public OpenAIService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }


    public String generateNaturalLanguageExplanation(List<Map<String, Object>> results) {
        // SQL 쿼리 결과를 자연어 설명으로 변환하는 프롬프트 생성
        String prompt = "다음은 회원 이름 목록입니다. 이를 한국어로 간단하고 자연스럽게 설명해 주세요:\n"
                        + results.toString()
                        + "\n단, 결과는 쿼리나 기술적인 용어를 포함하지 말고, 사용자 친화적으로 작성해 주세요.";
        // OpenAI API 요청
        String request = chatClient.prompt(prompt)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getContent();

        return request;
    }

}
