package com.nado.smartcare.openai.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Log4j2
public class InitDataLoader {

    private final VectorStore vectorStore;
    // 중복검사를 위해(파일명, hash) String타입 사용
    private final RedisTemplate<String, String> redisTemplate;

    @Value("classpath:/ai/intro.txt")
    private Resource txtResource;

    public InitDataLoader(VectorStore vectorStore, RedisTemplate<String, String> redisTemplate) {
        this.vectorStore = vectorStore;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() throws IOException {
        // 파일 이름 중복 검사
        String fileName = txtResource.getFilename();
        boolean alreadyProcessed = isFileAlreadyProcessed(fileName);

        if (alreadyProcessed) {
            log.info("File already processed: {}", fileName);
            return;
        }

        // 중복되지 않았다면 TXT 파일 이름을 읽고 Redis에 저장
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(txtResource.getInputStream()))) {

            // TXT 파일에 각 줄을 Documentfh todtjd
            List<Document> documents = br.lines()
                    .map(Document::new)  // 한 줄씩 Document 객체로
                    .collect(Collectors.toList());

            // 문서 분할
            TextSplitter splitter = new TokenTextSplitter(1000, 400, 10, 5000, true);

            // 분할된 결과를 벡터 스토어에 저장
            for (Document doc : documents) {
                List<Document> splittedDocs = splitter.split(doc);
                vectorStore.add(splittedDocs);
            }

            // 처리 완료 후, Redis에 "이 파일은 이미 처리됨"을 기록
            markFileAsProcessed(fileName);
            log.info("[TxtDataLoader] 파일 처리 완료: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 파일 이름 기준으로 중복 처리 여부 확인.
     * 예: Redis에 'processedFiles'라는 Set을 두고, 해당 파일이 있으면 중복 간주.
     */
    private boolean isFileAlreadyProcessed(String fileName) {
        return redisTemplate.opsForSet().isMember("processedFiles", fileName);
    }

    /**
     * 처리 완료된 파일을 Set에 등록.
     */
    private void markFileAsProcessed(String fileName) {
        redisTemplate.opsForSet().add("processedFiles", fileName);
    }
}
