package com.fitiz.csredisconsumer.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record StepCountUpdateData(UUID userId, String username, UUID challengeId, Integer steps,
                                  LocalDateTime createdAt) {
}
