package com.dinosaur.foodbowl.domain.follow.entity;

import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "follow")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "following_id", nullable = false, updatable = false)
  private User following;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "follower_id", nullable = false, updatable = false)
  private User follower;

  @Builder
  private Follow(User following, User follower) {
    this.following = following;
    this.follower = follower;
  }

  public boolean isFollowing(User user) {
    return following.equals(user);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Follow follow = (Follow) o;
    return following.equals(follow.following) && follower.equals(follow.follower);
  }

  @Override
  public int hashCode() {
    return Objects.hash(following, follower);
  }

  public static Follow of(User follower, User following) {
    return Follow.builder()
        .follower(follower)
        .following(following)
        .build();
  }
}