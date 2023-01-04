package com.dinosaur.foodbowl.domain.user.entity;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.global.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@ToString(of = {"loginId", "nickname", "introduce"})
public class User extends BaseEntity {

  private static final int MAX_LOGIN_ID_LENGTH = 40;
  private static final int MAX_PASSWORD_LENGTH = 512;
  private static final int MAX_NICKNAME_LENGTH = 40;
  private static final int MAX_INTRODUCE_LENGTH = 255;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @JoinColumn
  @ManyToOne(fetch = LAZY, cascade = ALL)
  private Thumbnail thumbnail;

  @Column(name = "login_id", nullable = false, unique = true, length = MAX_LOGIN_ID_LENGTH)
  private String loginId;

  @Column(name = "password", nullable = false, length = MAX_PASSWORD_LENGTH)
  private String password;

  @Column(name = "nickname", nullable = false, unique = true, length = MAX_NICKNAME_LENGTH)
  private String nickname;

  @Column(name = "introduce", unique = true, length = MAX_INTRODUCE_LENGTH)
  private String introduce;

  @Builder
  private User(Thumbnail thumbnail, String loginId, String password, String nickname,
      String introduce) {
    this.thumbnail = thumbnail;
    this.loginId = loginId;
    this.password = password;
    this.nickname = nickname;
    this.introduce = introduce;
  }
}
