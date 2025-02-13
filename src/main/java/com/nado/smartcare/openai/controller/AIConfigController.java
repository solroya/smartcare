package com.nado.smartcare.openai.controller;

import com.nado.smartcare.openai.service.AIModelService;
import com.nado.smartcare.openai.service.OllamaModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai-config")
@RequiredArgsConstructor
@Log4j2
public class AIConfigController {
    private final AIModelService aiModelService;
    private final OllamaModelService ollamaModelService;

    @GetMapping("/check-ollama")
    public ResponseEntity<Map<String, Object>> checkAiModel() {
        Map<String, Object> response = new HashMap<>();
        boolean ollamaRunning = isOllamaRunning();
        boolean mistralInstalled = false;

        response.put("ollamaRunning", ollamaRunning);

        if (ollamaRunning) {
            mistralInstalled = ollamaModelService.isMistralInstalled();
            response.put("mistralInstalled", mistralInstalled);

            // Mistral이 설치되어 있다면 추천 제공자로 설정
            response.put("recommendedProvider", mistralInstalled ? "ollama" : "openai");

            if (!mistralInstalled) {
                log.info("Ollama is running but Mistral model is not installed");
                response.put("message", "Mistral 모델이 설치되어 있지 않습니다. " +
                        "터미널에서 'ollama pull mistral' 명령어로 설치해주세요.");
            } else {
                log.info("Ollama is running with Mistral model installed");
            }
        } else {
            log.info("Ollama server is not running");
        }

        response.put("currentProvider", aiModelService.getCurrentProvider());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/provider")
    public ResponseEntity<Map<String, Object>> updateProvider(@RequestBody Map<String, String> request) {
        String newProvider = request.get("provider");
        String previousProvider = aiModelService.getCurrentProvider();
        Map<String, Object> response = new HashMap<>();

        try {
            // Ollama로 변경하려는 경우 추가 검증
            if ("ollama".equals(newProvider)) {
                if (!isOllamaRunning()) {
                    log.warn("Attempted to switch to Ollama but server is not running");
                    return ResponseEntity.badRequest()
                            .body(Map.of(
                                    "success", false,
                                    "message", "Ollama 서버가 실행되고 있지 않습니다."
                            ));
                }

                if (!ollamaModelService.isMistralInstalled()) {
                    log.warn("Attempted to switch to Ollama but Mistral model is not installed");
                    return ResponseEntity.badRequest()
                            .body(Map.of(
                                    "success", false,
                                    "message", "Mistral 모델이 설치되어 있지 않습니다."
                            ));
                }
            }

            // 설정 변경
            aiModelService.setProvider(newProvider);

            // 변경 로그 기록
            log.info("AI provider changed from '{}' to '{}'", previousProvider, newProvider);

            response.put("success", true);
            response.put("message", String.format("AI 제공자가 %s에서 %s(으)로 변경되었습니다.",
                    previousProvider, newProvider));
            response.put("newProvider", newProvider);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error updating AI provider", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "success", false,
                            "message", "설정 변경 중 오류가 발생했습니다."
                    ));
        }
    }

    private boolean isOllamaRunning() {
        try {
            log.info("Attempting to connect to Ollama server...");
            RestTemplate restTemplate = new RestTemplate();

            // 기본 URL로 요청
            ResponseEntity<String> response = restTemplate.getForEntity(
                    "http://localhost:11434",
                    String.class
            );

            // 응답 내용 확인
            if (response.getStatusCode().is2xxSuccessful() &&
                    response.getBody() != null &&
                    response.getBody().contains("Ollama is running")) {

                log.info("Ollama server detected and running");
                return true;
            }

            log.warn("Unexpected response from Ollama server: {}", response.getBody());
            return false;

        } catch (Exception e) {
            log.error("Error connecting to Ollama server: {}", e.getMessage());
            return false;
        }
    }
}
