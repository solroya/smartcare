package com.nado.smartcare.openai.entity.dto;

public record AssistantResponse(
        boolean success,
        String message,
        String sqlQuery
) {}
