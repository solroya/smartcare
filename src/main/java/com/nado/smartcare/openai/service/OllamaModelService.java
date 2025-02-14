package com.nado.smartcare.openai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class OllamaModelService {
    private final RestTemplate restTemplate;

    // 모델 태그 정보를 담을 클래스
    @Data
    public static class ModelInfo {
        private String name;
        private String digest;
        private long size;
    }

    // 설치된 모델 목록을 확인하는 메서드
    public List<ModelInfo> getInstalledModels() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    "http://localhost:11434/api/tags",
                    String.class
            );

            // JSON 응답을 파싱하여 모델 정보 추출
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            List<ModelInfo> models = new ArrayList<>();

            if (root.has("models")) {
                root.get("models").forEach(model -> {
                    ModelInfo info = new ModelInfo();
                    info.setName(model.get("name").asText());
                    info.setDigest(model.get("digest").asText());
                    info.setSize(model.get("size").asLong());
                    models.add(info);
                });
            }

            return models;

        } catch (Exception e) {
            log.error("Error fetching installed models", e);

            return Collections.emptyList();
        }
    }

    // Mistral 모델이 설치되어 있는지 확인하는 메서드
    public boolean isMistralInstalled() {
        return getInstalledModels().stream()
                .anyMatch(model -> model.getName().toLowerCase().contains("mistral"));
    }
}
