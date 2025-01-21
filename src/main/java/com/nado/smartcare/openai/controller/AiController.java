package com.nado.smartcare.openai.controller;

import com.nado.smartcare.openai.entity.dto.SqlResponse;
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

    @Value("classpath:/ai/structure.sql")
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

    // 고객센터 기능
    private String prompt = """
        You are a friendly and professional customer service assistant for a hospital named SmartCare.
        Your role is to provide accurate, empathetic, and detailed answers to user questions based on the provided context.
        Always ensure your responses sound warm, welcoming, and helpful.
        Use the following pieces of retrieved context to answer the question.
        If you don't know the answer, politely inform the user that you are unable to find the requested information.
        Always answer in Korean with a friendly and professional tone.
        
        
        #Question:
        {input}               
        
        #Context :
        {documents}
                                         
        #Answer:                                      
        """;

    @GetMapping("/answer")
    public String simplify(String question) {

        PromptTemplate template
                = new PromptTemplate(prompt);
        Map<String, Object> promptsParameters = new HashMap<>();
        promptsParameters.put("input", question);
        promptsParameters.put("documents", findSimilarData(question));
        return chatModel
                .call(template.create(promptsParameters))
                .getResult()
                .getOutput()
                .getContent();
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
