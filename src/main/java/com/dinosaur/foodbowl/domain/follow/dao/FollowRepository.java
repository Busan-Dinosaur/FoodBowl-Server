package com.dinosaur.foodbowl.domain.follow.dao;

import com.dinosaur.foodbowl.domain.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

}
