package com.nado.smartcare.openai.controller;

import com.nado.smartcare.openai.entity.dto.ChatRequest;
import com.nado.smartcare.openai.entity.dto.ChatResponse;
import com.nado.smartcare.openai.service.MedicalAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("medical")
@Log4j2
public class AIMedicalController {
    private final MedicalAIService medicalAIService;

    @PostMapping("/chat")
    @ResponseBody
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            // MedicalAIService의 generateResponse 메서드를 직접 사용
            String response = medicalAIService.generateResponse(request.message());

            return ResponseEntity.ok(
                    ChatResponse.success(
                            response,
                            medicalAIService.getAiModelService().getCurrentProvider()
                    )
            );

        } catch (Exception e) {
            log.error("Chat processing error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ChatResponse.error(
                            "죄송합니다. 응답 생성 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                    ));
        }
    }

}
