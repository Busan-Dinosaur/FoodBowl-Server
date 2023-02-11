package com.dinosaur.foodbowl.domain.follow.dao;

import com.dinosaur.foodbowl.domain.follow.entity.Follow;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

  long countByFollower(User follower);

  long countByFollowing(User following);

  boolean existsByFollowerAndFollowing(User follower, User following);

  Optional<Follow> findByFollowerAndFollowing(User follower, User following);
}
