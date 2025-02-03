package com.nado.smartcare.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import lombok.extern.log4j.Log4j2;

@EnableEncryptableProperties // 암호화된 설정들을 자동으로 복호화 ENC()
@Configuration
@Log4j2
public class JasyptConfig {
	private static final String JASYPT_PASSWORD_ENV_VAR = "JASYPT_ENCRYPTOR_PASSWORD";
	private final String password;
	public JasyptConfig(Environment env) {
		this.password = env.getProperty(JASYPT_PASSWORD_ENV_VAR);
		
		if (this.password == null || this.password.isEmpty()) {
			log.error("JASYPT_ENCRYPTOR_PASSWORD is not set or empty");
			throw new IllegalStateException("JASYPT_ENCRYPTOR_PASSWORD must be set");
		}
		
		log.info("JASYPT_ENCRYPTOR_PASSWORD is set and not empty");
	}
	
	@Bean("jasyptStringEncryptor")
	public StringEncryptor stringEncryptor() {
		PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
		SimpleStringPBEConfig config = new SimpleStringPBEConfig();
		
		config.setPassword(password);
		config.setPoolSize("1");
		config.setAlgorithm("PBEWithMD5AndDES");
		config.setStringOutputType("base64"); // 인코딩 방식 (A-Za-z0-9+/=)로 이루어진 문자열
		config.setKeyObtentionIterations("1000"); // 암호화 작업의 반복 횟수
		
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
		encryptor.setConfig(config);
		
		log.info("Jasypt StringEncryptor configured successfully");
		
		return encryptor;
		
	}
}
