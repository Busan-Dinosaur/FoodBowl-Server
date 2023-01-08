package com.dinosaur.foodbowl.domain.user.entity.role;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@ToString(of = {"name"})
@Getter
public class Role {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "name")
  private String name;

  public static Role getRoleBy(RoleType type) {
    return Role.builder()
        .id(type.id)
        .name(type.name)
        .build();
  }

  @Builder
  private Role(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  @Getter
  public enum RoleType {
    USER(1, "ROLE_USER"),
    ADMIN(2, "ROLE_ADMIN");

    private final long id;
    private final String name;

    RoleType(long id, String name) {
      this.id = id;
      this.name = name;
    }
  }
}
