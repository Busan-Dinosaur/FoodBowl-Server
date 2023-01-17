package com.dinosaur.foodbowl.domain.follow.dao;

import com.dinosaur.foodbowl.domain.follow.entity.Follow;
import com.dinosaur.foodbowl.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

  long countByFollower(User follower);

  long countByFollowing(User following);
}
