package com.nextdaydelivery.ai_response.domain.repository;

import com.nextdaydelivery.ai_response.domain.entity.AiResponse;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiResponseRepository extends JpaRepository<AiResponse, UUID> {
}
