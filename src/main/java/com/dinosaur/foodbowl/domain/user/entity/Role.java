package com.dinosaur.foodbowl.domain.user.entity;

import static com.dinosaur.foodbowl.global.error.ErrorCode.TYPE_INVALID;
import static jakarta.persistence.GenerationType.IDENTITY;

import com.dinosaur.foodbowl.global.error.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "role")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
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

        public static RoleType from(String name) {
            return Arrays.stream(RoleType.values())
                    .filter(type -> type.toString().equals(name))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(name, "name", TYPE_INVALID));
        }
    }
}
