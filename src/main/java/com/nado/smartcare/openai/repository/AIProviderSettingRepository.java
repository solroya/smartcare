package com.nado.smartcare.openai.repository;

import com.nado.smartcare.openai.entity.AIProviderSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AIProviderSettingRepository extends JpaRepository<AIProviderSetting, Long> {
    Optional<AIProviderSetting> findFirstByOrderByUpdatedAtDesc();
}
