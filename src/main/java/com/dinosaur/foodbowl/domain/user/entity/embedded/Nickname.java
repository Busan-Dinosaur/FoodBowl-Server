package com.dinosaur.foodbowl.domain.user.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"nickname"}, callSuper = false)
public class Nickname {

    public static final int MAX_NICKNAME_LENGTH = 45;
    public static final String PATTERN = "^[a-zA-Z0-9가-힣]{1,16}";
    public static final String NICKNAME_INVALID = "닉네임은 1~16자 한글, 영어, 숫자만 가능합니다.";

    @Pattern(regexp = PATTERN, message = NICKNAME_INVALID)
    @Column(name = "nickname", nullable = false, unique = true, length = MAX_NICKNAME_LENGTH)
    private String nickname;

    @Builder
    private Nickname(String nickname) {
        this.nickname = nickname;
    }

    public static Nickname from(String nickname) {
        return Nickname.builder()
                .nickname(nickname)
                .build();
    }
}
