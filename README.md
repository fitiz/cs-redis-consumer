### Challenge service Kafka consumer for Redis

- Kafka consumer for updating the leaderbord sorted set in Redis

```java
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
```
