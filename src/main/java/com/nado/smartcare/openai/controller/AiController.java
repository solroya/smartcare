package com.nado.smartcare.openai.controller;

import com.nado.smartcare.openai.entity.dto.SqlResponse;
import com.nado.smartcare.openai.service.AIModelService;
import com.nado.smartcare.openai.service.OpenAIService;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/ai")
public class AiController {

    private final AIModelService aiModelService;
    private final VectorStore vectorStore;

    public AiController(AIModelService aiModelService,@Qualifier("customVectorStore") VectorStore vectorStore) {
        this.aiModelService = aiModelService;
        this.vectorStore = vectorStore;
    }

    /*@Value("classpath:/ai/structure.sql")
    private Resource ddlResource;

    @Value("classpath:/ai/sql-prompt-template.st")
    private Resource sqlPromptTemplateResource;

    private final ChatClient chatClient;
    private final JdbcTemplate jdbcTemplate;

    private final OpenAIService openAIService;

    private final ChatModel chatModel;
    private final VectorStore vectorStore;


    public AiController(ChatClient.Builder chatClient, JdbcTemplate jdbcTemplate, OpenAIService openAIService, ChatModel chatModel,@Qualifier("customVectorStore") VectorStore vectorStore) {
        this.chatClient = chatClient.build();
        this.jdbcTemplate = jdbcTemplate;
        this.openAIService = openAIService;
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
    }


    @PostMapping("/sql")
    public SqlResponse sql(@RequestParam(name = "question") String question) throws IOException {
        log.info("User question: {}", question);

        // Load schema
        String schema = ddlResource.getContentAsString(Charset.defaultCharset()); //UTF-8

        // LLM 자동 쿼리 생성
        log.info("Calling LLM to generate SQL query...");
        String query = chatClient.prompt()
                .user(userSpec -> userSpec
                        .text(sqlPromptTemplateResource)
                        .param("question", question)
                        .param("ddl", schema))
                .call()
                .content();

        log.info("Generated SQL query: {}", query);

        // Clean SQL query
        query = query.trim();
        if (query.endsWith(";")) {
            query = query.substring(0, query.length() - 1);
        }
        log.info("Sanitized SQL query: {}", query);

        // If the query is a SELECT statement
        if(query.toLowerCase().startsWith("select")) {
            log.info("Executing the generated query...");

            List<Map<String, Object>> results = jdbcTemplate.queryForList(query);
            log.info("Query execution complete. Row size: {}", results.size());

            // Generate natural language explanation using OpenAI
            log.info("Generating natural language explanation...");
            String naturalResponse = openAIService.generateNaturalLanguageExplanation(results);

            return new SqlResponse(query, jdbcTemplate.queryForList(query), naturalResponse);
        }
        return new SqlResponse(query, List.of(), null); // null
    }

     */

    // 고객센터 기능
    private String prompt = """
    You are a knowledgeable and professional customer service AI for SmartCare Hospital.
    Your role is to provide helpful and accurate information while maintaining a good balance between detail and conciseness.
    
    Response Guidelines:
    1. Start with a warm but brief greeting when appropriate
    2. Provide clear, accurate answers based on the context
    3. Include relevant details that might be helpful, but avoid overwhelming information
    4. For questions you can't fully answer with the given context:
       - Share what information you do know
       - Politely explain which specific parts require customer service contact
       - Provide the customer service number (062-362-7897)
    5. Keep a professional yet friendly tone
    6. If answering medical questions, always recommend consulting with medical professionals
    7. End with a brief, helpful closing when appropriate
    
    Remember to:
    - Answer in Korean
    - Be clear and professional
    - Stay factual and accurate
    - Show appropriate empathy
    - Focus on being helpful
    - Hospital is "병원"
    
    #Question:
    {input}               
    
    #Context:
    {documents}
                                     
    #Answer:                                      
    """;

    @GetMapping("/answer")
    public ResponseEntity<Map<String, String>> answer(@RequestParam String question) {
        try {
            // PromptTemplate 생성 및 파라미터 설정
            PromptTemplate template = new PromptTemplate(prompt);
            Map<String, Object> promptParameters = new HashMap<>();
            promptParameters.put("input", question);
            promptParameters.put("documents", findSimilarData(question));

            // AI 응답 생성
            String response = aiModelService.getResponseWithPrompt(prompt, promptParameters);

            // JSON 응답 생성
            Map<String, String> jsonResponse = new HashMap<>();
            jsonResponse.put("response", response);
            jsonResponse.put("provider", aiModelService.getCurrentProvider());

            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("response", "죄송합니다. 응답 생성 중 오류가 발생했습니다.");
            errorResponse.put("provider", "System");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
    // # 5.단계 - 검색기(Retriever) 생성---|(Question)<---유사도 검색(similarity)
    // 문서에 포홤되어 있는 정보를 검색하고 생성
    private String findSimilarData(String question) {
        List<Document> documents =
                vectorStore.similaritySearch(SearchRequest
                        .query(question)
                        .withTopK(2));
        System.out.println(documents.toString());
        return documents
                .stream()
                .map(document -> document.getContent().toString())
                .collect(Collectors.joining());
    }

}
