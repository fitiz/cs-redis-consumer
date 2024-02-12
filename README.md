### Challenge service Kafka consumer for Redis

- Kafka consumer for updating the leaderboard sorted set in Redis
- Publishes "leaderboard-change" topic indicating which leaderboard changed at which timestamp

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

        log.info("Step count consumed, [user: {}, step count: {}]", stepCountUpdateData.username(), steps);
        leaderboardRedisRepository.updateSteps(leaderboardCollectionKey, steps, stepCountUpdateData.username());
        log.info("Step count added to redis leaderboard...");

        long changeTimestampMs = getTimestampMs(stepCountUpdateData.createdAt());
        kafkaLeaderboardChangeTemplate.send(LEADERBOARD_CHANGE_TOPIC,
                new LeaderboardChangeTime(changeTimestampMs));
        log.info("Leaderboard change timestamp {} published to Kafka...", changeTimestampMs);
    }
```
