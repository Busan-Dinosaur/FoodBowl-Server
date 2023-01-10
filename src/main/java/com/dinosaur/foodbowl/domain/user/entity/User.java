package com.dinosaur.foodbowl.domain.user.entity;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.entity.role.Role;
import com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType;
import com.dinosaur.foodbowl.domain.user.entity.role.UserRole;
import com.dinosaur.foodbowl.global.entity.BaseEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
@ToString(of = {"loginId", "nickname", "introduce"})
public class User extends BaseEntity {

  public static final int MAX_LOGIN_ID_LENGTH = 40;
  public static final int MAX_PASSWORD_LENGTH = 512;
  public static final int MAX_NICKNAME_LENGTH = 40;
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

  @Column(name = "introduce", unique = true, length = MAX_INTRODUCE_LENGTH)
  @Getter
  private String introduce;

  @OneToMany(mappedBy = "user", cascade = ALL, orphanRemoval = true)
  private final List<UserRole> userRole = new ArrayList<>();

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
}
