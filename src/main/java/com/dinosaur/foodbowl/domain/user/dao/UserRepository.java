package com.dinosaur.foodbowl.domain.user.dao;

import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.domain.user.entity.embedded.Nickname;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByLoginId(String loginId);

  boolean existsByNickname(Nickname nickname);

  Optional<User> findByLoginId(String loginId);

  @Query("select u from User u where u in (select f.following from Follow f where f.follower=:user)")
  List<User> findFollowingsByUser(@Param("user") User user, Pageable pageable);

  @Query("select u from User u where u in (select f.follower from Follow f where f.following=:user)")
  List<User> findFollowersByUser(@Param("user") User user, Pageable pageable);

}
