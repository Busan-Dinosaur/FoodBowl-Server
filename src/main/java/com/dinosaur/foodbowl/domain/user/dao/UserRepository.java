package com.dinosaur.foodbowl.domain.user.dao;

import com.dinosaur.foodbowl.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByLoginId(String loginId);

  boolean existsByNickname(String nickname);
}
