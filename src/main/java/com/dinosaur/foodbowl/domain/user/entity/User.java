package com.dinosaur.foodbowl.domain.user.entity;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import com.dinosaur.foodbowl.domain.follow.entity.Follow;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.entity.role.Role;
import com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType;
import com.dinosaur.foodbowl.domain.user.entity.role.UserRole;
import com.dinosaur.foodbowl.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class User extends BaseEntity {

  public static final int MAX_LOGIN_ID_LENGTH = 45;
  public static final int MAX_PASSWORD_LENGTH = 512;
  public static final int MAX_NICKNAME_LENGTH = 45;
  public static final int MAX_INTRODUCE_LENGTH = 255;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  @Getter
  private Long id;

  @ManyToOne(fetch = LAZY, cascade = ALL)
  @JoinColumn(name = "thumbnail_id")
  private Thumbnail thumbnail;

  @Column(name = "login_id", nullable = false, unique = true, length = MAX_LOGIN_ID_LENGTH)
  @Getter
  private String loginId;

  @Column(name = "password", nullable = false, length = MAX_PASSWORD_LENGTH)
  @Getter
  private String password;

  @Column(name = "nickname", nullable = false, unique = true, length = MAX_NICKNAME_LENGTH)
  @Getter
  private String nickname;

  @Column(name = "introduce", length = MAX_INTRODUCE_LENGTH)
  @Getter
  private String introduce;

  @Getter
  @OneToMany(mappedBy = "user", cascade = ALL, orphanRemoval = true)
  private final Set<UserRole> userRole = new HashSet<>();

  @OneToMany(mappedBy = "follower", cascade = ALL, orphanRemoval = true)
  private final Set<Follow> followingList = new HashSet<>();

  @OneToMany(mappedBy = "user", cascade = REMOVE)
  private final List<Post> posts = new ArrayList<>();

  @Builder
  private User(Thumbnail thumbnail, String loginId, String password, String nickname,
      String introduce) {
    this.thumbnail = thumbnail;
    this.loginId = loginId;
    this.password = password;
    this.nickname = nickname;
    this.introduce = introduce;
    this.assignRole(RoleType.ROLE_회원);
  }

  public void assignRole(RoleType roleType) {
    this.userRole.add(UserRole.builder()
        .user(this)
        .role(Role.getRoleBy(roleType))
        .build());
  }

  public Optional<String> getThumbnailURL() {
    return thumbnail == null ? Optional.empty() : Optional.of(thumbnail.getPath());
  }

  public boolean containsRole(RoleType roleType) {
    return userRole.contains(UserRole.builder()
        .user(this)
        .role(Role.getRoleBy(roleType))
        .build());
  }

  public void updateProfile(Thumbnail thumbnail, String introduce) {
    if (thumbnail != null) {
      this.thumbnail = thumbnail;
    }
    if (introduce != null) {
      this.introduce = introduce;
    }
  }

  public void follow(User other) {
    followingList.add(Follow.builder()
        .follower(this)
        .following(other)
        .build());
  }

  public void unfollow(User other) {
    followingList.removeIf(follow -> follow.getFollowing().equals(other));
  }

  public boolean isFollowing(User other) {
    return followingList.stream()
        .anyMatch(follow -> follow.getFollowing().equals(other));
  }

  public long getPostCount() {
    return posts.size();
  }
}
