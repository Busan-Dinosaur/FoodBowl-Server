package com.dinosaur.foodbowl.domain.user.entity;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import com.dinosaur.foodbowl.domain.follow.entity.Follow;
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
@ToString(of = {"loginId", "nickname", "introduce", "userRole"})
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

  @Getter
  @OneToMany(mappedBy = "follower", cascade = ALL, orphanRemoval = true)
  private final Set<Follow> followingList = new HashSet<>();

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

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", loginId='" + loginId + '\'' +
        ", password='" + password + '\'' +
        ", nickname='" + nickname + '\'' +
        ", introduce='" + introduce + '\'' +
        ", userRole=" + userRole +
        '}';
  }

  public void follow(User user) {
    followingList.add(Follow.of(this, user));
  }

  public void unfollow(User user) {
    followingList.removeIf(follow -> follow.isFollowing(user));
  }
}
