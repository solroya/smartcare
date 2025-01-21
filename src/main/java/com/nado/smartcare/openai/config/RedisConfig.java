package com.nado.smartcare.openai.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.RedisVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;


@Configuration
public class RedisConfig {

    /*docker run -d --name redis-stack -p 6379:6379 redis/redis-stack:latest*/

    @Bean
    public JedisPooled jedisPooled() {
        return new JedisPooled("localhost", 6379);
    }

    @Bean(name = "customVectorStore")
    public VectorStore vectorStore(JedisPooled jedisPooled, EmbeddingModel embeddingModel) {
        RedisVectorStore.RedisVectorStoreConfig config = RedisVectorStore.RedisVectorStoreConfig.builder()
                .withIndexName("custom-index")
                .withPrefix("custom-prefix")
                .withMetadataFields(
                        RedisVectorStore.MetadataField.tag("count"),
                        RedisVectorStore.MetadataField.numeric("year")
                )
                .build();
        return new RedisVectorStore(config, embeddingModel, jedisPooled, true);
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        return new OpenAiEmbeddingModel(new OpenAiApi(System.getenv("OPENAI_API_KEY")));
    }

}
