package com.dinosaur.foodbowl.domain.auth.dto.request;

import com.dinosaur.foodbowl.domain.auth.dto.AuthFieldError.Message;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginRequestDto {

    @Pattern(regexp = "^[a-zA-Z0-9_]{4,12}", message = Message.LOGIN_ID_INVALID)
    private String loginId;
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$", message = Message.PASSWORD_INVALID)
    private String password;

    @Builder
    private LoginRequestDto(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }
}
