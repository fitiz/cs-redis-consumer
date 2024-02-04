package com.fitiz.csredisconsumer.service;

import com.fitiz.csredisconsumer.model.LeaderboardChangeData;
import com.fitiz.csredisconsumer.model.StepCountUpdateData;
import com.fitiz.csredisconsumer.repository.LeaderboardRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
@RequiredArgsConstructor
public class StepCountRedisConsumer {

    @Value("${prop.config.broker-properties.leaderboard-change-topic}")
    public String LEADERBOARD_CHANGE_TOPIC;

    public static final String LEADERBOARD_KEY_PREFIX = "challenge-";

    private final KafkaTemplate<String, LeaderboardChangeData> kafkaLeaderboardChangeTemplate;
    private final LeaderboardRedisRepository leaderboardRedisRepository;

    @KafkaListener(
            topics = {"${prop.config.broker-properties.step-count-topic}"},
            groupId = "${prop.config.broker-properties.step-count-topic-redis-consumer-group-id}",
            properties = {"spring.json.value.default.type=com.fitiz.csredisconsumer.model.StepCountUpdateData"}
    )
    public void stepCountRedisConsumer(ConsumerRecord<String, StepCountUpdateData> record) {
        var stepCountUpdateData = record.value();
        var steps = stepCountUpdateData.steps();
        var leaderboardKey = LEADERBOARD_KEY_PREFIX + stepCountUpdateData.challengeId();

        log.info("Step count consumed, [user: {}, step count: {}]", stepCountUpdateData.username(), steps);
        leaderboardRedisRepository.updateSteps(leaderboardKey, steps, stepCountUpdateData.username());
        log.info("Step count added to redis leaderboard...");

        long changeTimestampMs = getTimestampMs(stepCountUpdateData.createdAt());
        kafkaLeaderboardChangeTemplate.send(LEADERBOARD_CHANGE_TOPIC,
                new LeaderboardChangeData(leaderboardKey, changeTimestampMs));
        log.info("Leaderboard change timestamp {} published to Kafka...", changeTimestampMs);
    }

    public long getTimestampMs(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}