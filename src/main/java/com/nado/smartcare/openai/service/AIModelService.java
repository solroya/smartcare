package com.nado.smartcare.openai.service;

import com.nado.smartcare.openai.entity.AIProviderSetting;
import com.nado.smartcare.openai.repository.AIProviderSettingRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class AIModelService {

    private final OpenAIService openAIService;
    private final OllamaService ollamaService;
    private final AIProviderSettingRepository providerSettingRepository;

    @Value("${ai.default-provider:openai}")
    private String defaultProvider;

    @PostConstruct
    public void init() {
        // 서비스 시작 시 저장된 설정 로드
        providerSettingRepository.findFirstByOrderByUpdatedAtDesc()
                .ifPresent(setting -> {
                    log.info("Loaded provider setting: {}", setting.getProvider());
                });
    }

    public String getCurrentProvider() {
        return providerSettingRepository.findFirstByOrderByUpdatedAtDesc()
                .map(AIProviderSetting::getProvider)
                .orElse(defaultProvider);
    }

    public void setProvider(String provider) {
        AIProviderSetting setting = providerSettingRepository.findFirstByOrderByUpdatedAtDesc()
                .orElse(new AIProviderSetting(provider));

        String oldProvider = setting.getProvider();
        setting.updateProvider(provider);
        providerSettingRepository.save(setting);

        log.info("AI provider changed from {} to {}", oldProvider, provider);
    }

    // 동기식 응답 메서드
    public String getResponse(String message) {
        String provider = getCurrentProvider();
        log.info("Getting response using provider: {}", provider);

        // 현재 설정된 제공자에 따라 서비스 선택
        if ("ollama".equals(provider)) {
            return ollamaService.getResponse(message);
        } else {
            return openAIService.getResponse(message);
        }
    }

    // Prompt 명령어 메서드
    public String getResponseWithPrompt(String promptTemplate, Map<String, Object> parameters) {
        String provider = getCurrentProvider();
        log.info("Getting response using provider: {} with prompt template", provider);

        if ("ollama".equals(provider)) {
            return ollamaService.getResponseWithPrompt(promptTemplate, parameters);
        } else {
            return openAIService.getResponseWithPrompt(promptTemplate, parameters);
        }
    }

}
