package com.nado.smartcare.openai.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.RedisVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;


@Configuration
public class RedisConfig {

    /*docker run -d --name redis-stack -p 6379:6379 redis/redis-stack:latest*/

/*    docker run -d --name redis-stack -p 6379:6379 -p 8001:8001 redis/redis-stack:latest*/


    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public JedisPooled jedisPooled() {
        return new JedisPooled(redisHost, redisPort);
    }

    @Bean(name = "customVectorStore")
    public VectorStore vectorStore(JedisPooled jedisPooled, @Qualifier("embeddingModel") EmbeddingModel embeddingModel) {
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

    @Bean(name = "embeddingModel")
    public EmbeddingModel embeddingModel() {
        return new OpenAiEmbeddingModel(new OpenAiApi(System.getenv("OPENAI_API_KEY")));
    }

}
