package com.dinosaur.foodbowl.domain.user.entity.role;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "role")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@ToString(of = {"roleType"})
@Getter
public class Role {

  private static final int MAX_NAME_LENGTH = 45;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "name", nullable = false, updatable = false, unique = true, length = MAX_NAME_LENGTH)
  private RoleType roleType;

  public static Role getRoleBy(RoleType type) {
    return Role.builder()
        .id(type.id)
        .roleType(type)
        .build();
  }

  @Builder
  private Role(Long id, RoleType roleType) {
    this.id = id;
    this.roleType = roleType;
  }

  public enum RoleType {
    ROLE_회원(1),
    ROLE_관리자(2);

    private final long id;

    RoleType(long id) {
      this.id = id;
    }
  }
}
