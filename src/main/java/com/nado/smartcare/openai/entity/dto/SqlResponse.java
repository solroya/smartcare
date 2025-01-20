package com.nado.smartcare.openai.entity.dto;

import java.util.List;
import java.util.Map;

public record SqlResponse(
        String sqlQuery,
        List<Map<String, Object>> results,
        String naturalResponse
) {
}
