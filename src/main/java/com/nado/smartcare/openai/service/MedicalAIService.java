package com.nado.smartcare.openai.service;

import com.nado.smartcare.openai.entity.MedicalDataChangeEvent;
import com.nado.smartcare.patient.domain.PatientRecordCard;
import com.nado.smartcare.patient.domain.dto.type.ClinicStatus;
import com.nado.smartcare.patient.repository.PatientRecordCardRepository;
import com.nado.smartcare.reservation.domain.Reservation;
import com.nado.smartcare.reservation.repository.ReservationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class MedicalAIService {

    private final VectorStore vectorStore;
    private final AIModelService aiModelService;
    private final PatientRecordCardRepository patientRecordCardRepository;
    private final ReservationRepository reservationRepository;

    // AIModelService getter 추가
    public AIModelService getAiModelService() {
        return aiModelService;
    }

    // 초기 데이터 로딩
    @Transactional(readOnly = true)
    @PostConstruct
    public void init() {
        try {
            log.info("초기 의료 데이터 인덱싱 시작...");
            indexMedicalRecords();
//            indexReservations();
            log.info("초기 의료 데이터 인덱싱 완료");
        } catch (Exception e) {
            log.error("초기 데이터 인덱싱 중 오류 발생", e);
        }
    }

    // 이벤트 리스너 - 데이터 변경 처리
    @EventListener
    @Transactional(readOnly = true)
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void handleMedicalDataChange(MedicalDataChangeEvent event) {
        try {
            log.info("데이터 변경 이벤트 수신: type={}, recordId={}, description={}",
                    event.getType(), event.getRecordId(), event.getChangeDescription());

            switch (event.getType()) {
                case "MEDICAL_RECORD" -> indexMedicalRecords();
//                case "RESERVATION" -> indexReservations();
                default -> log.warn("알 수 없는 이벤트 유형: {}", event.getType());
            }

            log.info("데이터 변경 이벤트 처리 완료: {}", event.getChangeDescription());
        } catch (Exception e) {
            log.error("이벤트 처리 중 오류 발생", e);
            throw e;  // 재시도를 위해 예외를 다시 던집니다
        }
    }

    // 이벤트 처리 실패 시 복구 처리
    @Recover
    public void handleMedicalDataChangeFailure(Exception e, MedicalDataChangeEvent event) {
        log.error("데이터 변경 이벤트 처리 최종 실패: event={}", event, e);
        // 여기에 관리자 알림 로직을 추가할 수 있습니다
    }

    // 의료 기록을 Vector Store에 저장하기 위한 메서드
    public void indexMedicalRecords() {
        List<PatientRecordCard> records = patientRecordCardRepository.findAll();
        List<Document> documents = records.stream().map(record -> {
            // 본문 내용 구성은 동일하게 유지
            String content = String.format("""
            이 진료 기록은 %s 환자의 %s 진료에 대한 내용입니다.
            진료는 %s에 %s 의사에 의해 수행되었으며, 현재 상태는 %s입니다.
            
            진단 정보:
            - 진단 분류: %s
            - 진단명: %s
            
            진료 소견:
            %s
            
            진료 번호는 %d입니다.
            """,
                    record.getMember().getMemberName(),
                    record.getClinicName(),
                    record.getClinicDate(),
                    record.getEmployee().getEmployeeName(),
                    record.getClinicStatus(),
                    record.getDiseaseCategory().getCategoryName(),
                    record.getDiseaseList().getDiseaseName(),
                    record.getClinicManifestation(),
                    record.getPatientRecordNo()
            );

            // Map<String, Object> 타입으로 메타데이터 생성
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("type", "medical_record");
            metadata.put("recordId", record.getPatientRecordNo());
            metadata.put("clinicName", record.getClinicName());
            metadata.put("diseaseCode", record.getDiseaseList().getDiseaseCategory().getCategoryName());
            metadata.put("diseaseName", record.getDiseaseList().getDiseaseName());
            metadata.put("categoryName", record.getDiseaseCategory().getCategoryName());
            metadata.put("doctorName", record.getEmployee().getEmployeeName());
            metadata.put("status", record.getClinicStatus().toString());  // enum을 문자열로 변환
            metadata.put("patientName", record.getMember().getMemberName());

            // LocalDate를 문자열로 저장
            metadata.put("clinicDate", record.getClinicDate().toString());
            metadata.put("reservationDate",
                    record.getClinicReservationDate() != null ?
                            record.getClinicReservationDate().toString() : null);

            return new Document(content, metadata);
        }).collect(Collectors.toList());

        vectorStore.add(documents);
        log.info("Indexed {} medical records with enhanced metadata", documents.size());
    }


    /*// 예약 정보를 Vector Store에 저장하기 위한 메서드
    @Transactional(readOnly = true)
    public void indexReservations() {
        List<Reservation> reservations = reservationRepository.findAllWithEmployee();
        List<Document> documents = reservations.stream().map(reservation -> {
            String content = String.format("""
                예약 번호: %d
                예약일: %s
                시간대: %s
                환자명: %s
                담당의: %s
                예약상태: %s
                """,
                    reservation.getReservationNo(),
                    reservation.getReservationDate(),
                    reservation.getTimeSlot(),
                    reservation.getMember().getMemberName(),
                    reservation.getEmployee().getEmployeeName(),
                    reservation.getReservationStatus()
            );

            return new Document(content, Map.of(
                    "type", "reservation",
                    "reservationId", reservation.getReservationNo().toString()
            ));
        }).collect(Collectors.toList());

        vectorStore.add(documents);
        log.info("Indexed {} reservations", documents.size());
    }*/

    // AI 응답을 생성하는 메서드
    public String generateResponse(String question) {
        // 검색 결과 수 증가 및 유사도 임계값 설정
        List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.query(question)
                        .withTopK(5)  // 더 많은 문서 검색
                        .withSimilarityThreshold(0.3)  // 유사도 임계값 설정
        );

        // 검색된 문서들의 메타데이터를 활용한 컨텍스트 강화
        String context = processSearchResults(relevantDocs, question);

        String promptTemplate = """
            당신은 SmartCare 병원의 의료 AI 어시스턴트입니다.
            아래의 의료 정보를 바탕으로 질문에 전문적이고 정확하게 답변해주세요.
            
            참고할 정보:
            {context}
            
            질문: {question}
            
            답변 시 주의사항:
            1. 진료 기록의 구체적인 내용을 참조하여 답변하세요.
            2. 질병명이나 진단 정보는 정확하게 언급하세요.
            3. 진료 상태나 예약 정보를 명확히 설명하세요.
            4. 의료진의 소견을 인용할 때는 출처를 명시하세요.
            5. 확실하지 않은 정보는 "확인이 필요합니다"라고 말씀해주세요.
            
            답변:
            """;

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("context", context);
        parameters.put("question", question);
        parameters.put("input", question);  // 통계를 위해 input 파라미터도 추가

        // AI 응답 생성
        return aiModelService.getResponseWithPrompt(promptTemplate, parameters);
    }

    private String processSearchResults(List<Document> documents, String question) {
        StringBuilder context = new StringBuilder();

        boolean isDateRelated = question.contains("언제") || question.contains("날짜") || question.contains("기간");
        boolean isDiseaseRelated = question.contains("질병") || question.contains("진단") || question.contains("증상");
        boolean isStatusRelated = question.contains("상태") || question.contains("진행");

        for (Document doc : documents) {
            try {
                Map<String, Object> metadata = doc.getMetadata();

                // 날짜 정보 파싱
                LocalDate clinicDate = null;
                String clinicDateStr = (String) metadata.get("clinicDate");
                if (clinicDateStr != null) {
                    try {
                        clinicDate = LocalDate.parse(clinicDateStr);
                    } catch (Exception e) {
                        log.warn("날짜 파싱 실패: {}", clinicDateStr);
                    }
                }

                // 상태 정보 파싱
                ClinicStatus status = null;
                String statusStr = (String) metadata.get("status");
                if (statusStr != null) {
                    try {
                        status = ClinicStatus.valueOf(statusStr);
                    } catch (Exception e) {
                        log.warn("상태 파싱 실패: {}", statusStr);
                    }
                }

                context.append("=== 진료 정보 ===\n");

                if (isDateRelated && clinicDate != null) {
                    if (clinicDate.isAfter(LocalDate.now().minusMonths(1))) {
                        context.append("[최근 진료] ");
                    }
                    context.append(String.format("진료일: %s\n", clinicDate));
                }

                if (isStatusRelated && status != null) {
                    context.append(String.format("진료 상태: %s\n", status));
                }

                if (isDiseaseRelated) {
                    String diseaseName = (String) metadata.get("diseaseName");
                    if (diseaseName != null) {
                        context.append(String.format("진단명: %s\n", diseaseName));
                    }
                }

                context.append(doc.getContent()).append("\n\n");

            } catch (Exception e) {
                log.warn("문서 처리 중 오류 발생: {}", e.getMessage());
            }
        }

        return context.toString();
    }
}
