package com.fitiz.csredisconsumer.repository;

import com.fitiz.csredisconsumer.provider.LeaderboardRedisProvider;
import org.springframework.stereotype.Service;

@Service
public class LeaderboardRedisRepository {

  private final LeaderboardRedisProvider leaderboardRedisProvider;

  public LeaderboardRedisRepository(LeaderboardRedisProvider leaderboardRedisProvider) {
    this.leaderboardRedisProvider = leaderboardRedisProvider;
  }
  public void updateSteps(String leaderboardId, Integer steps, String username) {
    leaderboardRedisProvider.updateSteps(leaderboardId, steps, username);
  }
}
