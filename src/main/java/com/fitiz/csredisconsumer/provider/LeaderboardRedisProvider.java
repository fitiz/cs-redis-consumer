package com.fitiz.csredisconsumer.provider;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.stereotype.Repository;

@Repository
public class LeaderboardRedisProvider {

  private StatefulRedisConnection<String, String> connection;

  public LeaderboardRedisProvider(RedisStandaloneConfiguration redisStandaloneConfiguration) {
    RedisURI redisURI = RedisURI.builder()
                                .withHost(redisStandaloneConfiguration.getHostName())
                                .withPort(redisStandaloneConfiguration.getPort()).build();
    RedisClient redisClient = RedisClient.create(redisURI);
    this.connection = redisClient.connect();
  }

  public Double updateSteps(String key, Integer steps, String username) {
    return connection.sync().zincrby(key, steps, username);
  }
}
