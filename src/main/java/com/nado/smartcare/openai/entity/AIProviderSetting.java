package com.nado.smartcare.openai.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_provider_setting")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AIProviderSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String provider; // ollama 또는 openAi

    private LocalDateTime updatedAt;

    public AIProviderSetting(String provider) {
        this.provider = provider;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProvider(String provider) {
        this.provider = provider;
        this.updatedAt = LocalDateTime.now();
    }
}