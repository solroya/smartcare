package com.nado.smartcare.openai.controller;

import com.nado.smartcare.openai.entity.dto.SqlResponse;
import com.nado.smartcare.openai.service.OpenAIService;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

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


    public AiController(ChatClient.Builder chatClient, JdbcTemplate jdbcTemplate, OpenAIService openAIService) {
        this.chatClient = chatClient.build();
        this.jdbcTemplate = jdbcTemplate;
        this.openAIService = openAIService;
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
}
