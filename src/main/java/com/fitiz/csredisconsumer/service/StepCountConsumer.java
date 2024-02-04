package com.fitiz.csredisconsumer.service;

import com.fitiz.csredisconsumer.model.LeaderboardChangeTime;
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
public class StepCountConsumer {

    @Value("${prop.config.broker-properties.leaderboard-change-topic}")
    public String LEADERBOARD_CHANGE_TOPIC;

    public static final String LEADERBOARD_KEY_PREFIX = "challenge-";

    private final KafkaTemplate<String, LeaderboardChangeTime> kafkaLeaderboardChangeTemplate;
    private final LeaderboardRedisRepository leaderboardRedisRepository;

    @KafkaListener(
            topics = {"${prop.config.broker-properties.step-count-topic}"},
            groupId = "${prop.config.broker-properties.step-count-topic-redis-consumer-group-id}",
            properties = {"spring.json.value.default.type=com.fitiz.csredisconsumer.model.StepCountUpdateData"}
    )
    public void stepCountRedisConsumer(ConsumerRecord<String, StepCountUpdateData> record) {
        var stepCountUpdateData = record.value();
        var steps = stepCountUpdateData.steps();
        var leaderboardCollectionKey = LEADERBOARD_KEY_PREFIX + stepCountUpdateData.challengeId();

        log.info("Step count consumed , user: {}, score: {} ", stepCountUpdateData.username(), steps);
        leaderboardRedisRepository.updateSteps(leaderboardCollectionKey, steps, stepCountUpdateData.username());
        log.info("Step count for user: {} , step count : {} added to redis leaderboard", stepCountUpdateData.username(), steps);

        kafkaLeaderboardChangeTemplate.send(LEADERBOARD_CHANGE_TOPIC,
                new LeaderboardChangeTime(getTimestampMs(stepCountUpdateData.createdAt())));
        log.info("Leaderboard change for user: {}, step count: {} timestamp published to kafka",
                stepCountUpdateData.username(), steps);
    }

    public long getTimestampMs(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}