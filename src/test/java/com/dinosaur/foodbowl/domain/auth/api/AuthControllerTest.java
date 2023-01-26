package com.dinosaur.foodbowl.domain.auth.api;

import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_INTRODUCE_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_LOGIN_ID_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_NICKNAME_LENGTH;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.ACCESS_TOKEN;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.REFRESH_TOKEN;
import static com.dinosaur.foodbowl.global.error.ErrorCode.LOGIN_ID_DUPLICATE;
import static com.dinosaur.foodbowl.global.error.ErrorCode.NICKNAME_DUPLICATE;
import static com.dinosaur.foodbowl.global.error.ErrorCode.PASSWORD_NOT_MATCH;
import static com.dinosaur.foodbowl.global.error.ErrorCode.USER_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.auth.dto.AuthFieldError;
import com.dinosaur.foodbowl.domain.auth.dto.request.LoginRequestDto;
import com.dinosaur.foodbowl.domain.auth.dto.request.SignUpRequestDto;
import com.dinosaur.foodbowl.domain.auth.dto.response.SignUpResponseDto;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType;
import com.dinosaur.foodbowl.global.error.BusinessException;
import com.dinosaur.foodbowl.global.error.ExceptionAdvice;
import jakarta.servlet.http.Cookie;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class AuthControllerTest extends IntegrationTest {

  @Nested
  @DisplayName("인증 상태 확인")
  class HealthCheck {

    /**
     * userId: 1L roles: ROLE_회원 expired: 2073년 1월 25일
     */
    private final long userId = 1L;
    private String validAccessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZXMiOiJST0xFX-2ajOybkCIsImlhdCI6MTY3NDY1MTAwOCwiZXhwIjozMjUxNDUxMDA4fQ.XFm88FEYzCcmkWlDW35_KjNQGF_TXcrIVdxF5vPIP0k";
    private String validRefreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2NzQ2NTUxNTAsImV4cCI6MTgzMjMzNTE1MH0.VclGNv3wakvPhJCrO8LdcFyBg7ggkbOKqAuTDtMTPhU";
    private String expiredAccessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZXMiOiJST0xFX-2ajOybkCIsImlhdCI6MTY3NDY1MTM2NiwiZXhwIjoxNjc0NjUxMzY2fQ.3FWhJDTZt3rhnLJ-gFrBL_fnNcoc8J-nFrWGtTOa5P8";
    private String expiredRefreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2NzQ2NTEzNjYsImV4cCI6MTY3NDY1MTM2Nn0.ZnJd4e3UsJOwzU15orSTmLH3F-6OiZuRnQdr79C5VMw";

    @Test
    @DisplayName("AT 존재하지 않으면 401 예외가 발생한다.")
    void should_throwException_when_accessTokenNotExist() throws Exception {
      mockMvc.perform(get("/health-check"))
          .andDo(print())
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("AT 유효하면 값을 반환한다.")
    void should_returnValue_when_accessTokenValid() throws Exception {
      mockMvc.perform(get("/health-check")
              .cookie(new Cookie(ACCESS_TOKEN.getName(), validAccessToken)))
          .andDo(print())
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("AT 유효하지 않으면 401 예외가 발생한다.")
    void should_throwException_when_accessTokenNotValid() throws Exception {
      mockMvc.perform(get("/health-check")
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "invalid-token")))
          .andDo(print())
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("AT 만료되고 RT 유효하면 값을 반환한다.")
    void should_returnValue_when_accessTokenExpired_refreshTokenValid() throws Exception {
      redisTemplate.opsForValue()
          .set(String.valueOf(userId), validRefreshToken, 60 * 1000L, TimeUnit.MILLISECONDS);

      mockMvc.perform(get("/health-check")
              .cookie(new Cookie(ACCESS_TOKEN.getName(), expiredAccessToken),
                  new Cookie(REFRESH_TOKEN.getName(), validRefreshToken)))
          .andDo(print())
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("AT 만료되고 RT 존재하지 않으면 401 예외가 발생한다.")
    void should_throwException_when_accessTokenExpired_refreshTokenNotExist() throws Exception {
      mockMvc.perform(get("/health-check")
              .cookie(new Cookie(ACCESS_TOKEN.getName(), expiredAccessToken)))
          .andDo(print())
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("AT 만료되고 RT 유효하지 않으면 401 예외가 발생한다.")
    void should_throwException_when_accessTokenExpired_refreshTokenNotValid() throws Exception {
      mockMvc.perform(get("/health-check")
              .cookie(new Cookie(ACCESS_TOKEN.getName(), expiredAccessToken),
                  new Cookie(REFRESH_TOKEN.getName(), "invalid-token")))
          .andDo(print())
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("AT 만료되고 RT 만료되면 401 예외가 발생한다.")
    void should_throwException_when_accessTokenExpired_refreshTokenExpired() throws Exception {
      mockMvc.perform(get("/health-check")
              .cookie(new Cookie(ACCESS_TOKEN.getName(), expiredAccessToken),
                  new Cookie(REFRESH_TOKEN.getName(), expiredRefreshToken)))
          .andDo(print())
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("AT 만료되고 RT 다르면 401 예외가 발생한다.")
    void should_throwException_when_accessTokenExpired_refreshTokenNotMatch() throws Exception {
      redisTemplate.opsForValue()
          .set(String.valueOf(userId), "other-token", 60 * 1000L, TimeUnit.MILLISECONDS);

      mockMvc.perform(get("/health-check")
              .cookie(new Cookie(ACCESS_TOKEN.getName(), expiredAccessToken),
                  new Cookie(REFRESH_TOKEN.getName(), validRefreshToken)))
          .andDo(print())
          .andExpect(status().isUnauthorized());
    }
  }

  @Nested
  @DisplayName("회원가입")
  class SignUp {

    private final Long userId = 1L;
    private final String validLoginId = "LoginId_123";
    private final String validPassword = "Password123";
    private final String validNickname = "바보gusah009";
    private final String validIntroduce = "Introduce";

    private MockMultipartFile thumbnail;
    private MultiValueMap<String, String> params;

    @BeforeEach
    void setUpSignUp() {
      thumbnail = thumbnailTestHelper.getThumbnailFile();
      params = new LinkedMultiValueMap<>();
      params.add("loginId", validLoginId);
      params.add("password", validPassword);
      params.add("nickname", validNickname);
      params.add("introduce", validIntroduce);
    }

    private ResultActions callSignUpApiWithoutThumbnail() throws Exception {
      return mockMvc.perform(multipart(HttpMethod.POST, "/sign-up")
              .queryParams(params)
              .contentType(MediaType.MULTIPART_FORM_DATA))
          .andDo(print());
    }

    private ResultActions callSignUpApi(MockMultipartFile thumbnail) throws Exception {
      return mockMvc.perform(multipart(HttpMethod.POST, "/sign-up")
              .file(thumbnail)
              .queryParams(params)
              .contentType(MediaType.MULTIPART_FORM_DATA))
          .andDo(print());
    }

    private void mockingValidResponse(Thumbnail thumbnail) {
      SignUpResponseDto responseDto = SignUpResponseDto.of(
          User.builder()
              .loginId(validLoginId)
              .password(validPassword)
              .nickname(validNickname)
              .introduce(validIntroduce)
              .thumbnail(thumbnail)
              .build());
      ReflectionTestUtils.setField(responseDto, "userId", userId);

      String accessToken = "accessToken";
      String refreshToken = "refreshToken";
      doReturn(responseDto).when(authService).signUp(any(SignUpRequestDto.class));
      doReturn(accessToken).when(tokenService).createAccessToken(anyLong(), any(RoleType.class));
      doReturn(refreshToken).when(tokenService).createRefreshToken(anyLong());
      doReturn(new Cookie(ACCESS_TOKEN.getName(), accessToken))
          .when(cookieUtils)
          .generateCookie(ACCESS_TOKEN.getName(), accessToken,
              (int) ACCESS_TOKEN.getValidMilliSecond() / 1000);
      doReturn(new Cookie(REFRESH_TOKEN.getName(), refreshToken))
          .when(cookieUtils)
          .generateCookie(REFRESH_TOKEN.getName(), refreshToken,
              (int) REFRESH_TOKEN.getValidMilliSecond() / 1000);
    }

    @Nested
    @DisplayName("회원가입 성공")
    class SignUpSuccess {

      @Test
      @DisplayName("썸네일이 있을 경우 회원가입은 성공한다.")
      void should_success_when_signUp_with_thumbnail() throws Exception {
        Thumbnail mockThumbnail = Thumbnail.builder()
            .height(200)
            .width(200)
            .path("/thumbnail/2022-01-11/random_name.jpeg")
            .build();
        mockingValidResponse(mockThumbnail);
        callSignUpApi(thumbnail)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").exists())
            .andExpect(jsonPath("$.loginId").value(validLoginId))
            .andExpect(jsonPath("$.nickname").value(validNickname))
            .andExpect(jsonPath("$.introduce").value(validIntroduce))
            .andExpect(jsonPath("$.thumbnailURL").exists())
            .andDo(document("sign-up",
                queryParameters(
                    parameterWithName("loginId")
                        .description("로그인 아이디 (최대 가능 길이 :" + MAX_LOGIN_ID_LENGTH),
                    parameterWithName("password")
                        .description("비밀번호 (최대 가능 길이 : 해싱해서 사용하기 때문에 없음"),
                    parameterWithName("nickname")
                        .description("유저 닉네임 (최대 가능 길이 :" + MAX_NICKNAME_LENGTH),
                    parameterWithName("introduce")
                        .description("유저 소개 (최대 가능 길이 :" + MAX_INTRODUCE_LENGTH)
                ),
                requestParts(
                    partWithName("thumbnail").description("유저가 등록할 썸네일").optional()
                ),
                responseCookies(
                    cookieWithName(ACCESS_TOKEN.getName()).description("사용자 인증에 필요한 access token"),
                    cookieWithName(REFRESH_TOKEN.getName()).description(
                        "인증 토큰 갱신에 필요한 refresh token")
                ),
                responseFields(
                    fieldWithPath("userId")
                        .description("DB에 저장된 user의 고유 ID 값"),
                    fieldWithPath("loginId")
                        .description("저장된 로그인 아이디"),
                    fieldWithPath("nickname")
                        .description("저장된 닉네임"),
                    fieldWithPath("introduce")
                        .description("저장된 소개글"),
                    fieldWithPath("thumbnailURL")
                        .description("썸네일 URL. 서버 URL 뒤에 그대로 붙이면 파일을 얻을 수 있음.")
                )));
      }

      @Test
      @DisplayName("썸네일이 없어도 회원가입은 성공한다.")
      void should_success_when_signUp_with_notInThumbnail() throws Exception {
        mockingValidResponse(null);
        callSignUpApiWithoutThumbnail()
            .andExpect(status().isCreated());
      }
    }

    @Nested
    @DisplayName("회원가입 유효성 검사")
    class SignUpValidation {

      @Test
      @DisplayName("너무 긴 요청 값일 경우 회원가입은 실패한다.")
      void should_failToSignUp_ifRequestValueIsLong() throws Exception {
        mockingValidResponse(null);

        params.set("loginId", "a".repeat(MAX_LOGIN_ID_LENGTH + 1));
        callSignUpApi(thumbnail).andExpect(status().isBadRequest());
        params.set("loginId", "loginId");

        params.set("nickname", "a".repeat(MAX_NICKNAME_LENGTH + 1));
        callSignUpApi(thumbnail).andExpect(status().isBadRequest());
        params.set("nickname", "nickname");

        params.set("introduce", "a".repeat(MAX_INTRODUCE_LENGTH + 1));
        callSignUpApi(thumbnail).andExpect(status().isBadRequest());
        params.set("introduce", "introduce");
      }

      @ParameterizedTest
      @ValueSource(strings = {"aaa", "abcde###", "oh-my-zsh", "한글을_사랑합시다", "cant blank",
          "0123456789012", "012345678901234567890123456789"})
      @DisplayName("로그인 아이디 유효성 검사 실패 시 회원가입은 실패한다.")
      void should_failToSignUp_when_validateNotPass(String invalidLoginId) throws Exception {
        mockingValidResponse(null);
        params.set("loginId", invalidLoginId);
        callSignUpApi(thumbnail)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                .value(ExceptionAdvice.getErrorMessage(invalidLoginId, "loginId",
                    AuthFieldError.LOGIN_ID_INVALID.getMessage())));
      }

      @ParameterizedTest
      @ValueSource(strings = {"aaaaaaa", "0123456789", "onlyEnglish", "###########", "cant blank",
          "         ", "012345678901234567890"})
      @DisplayName("비밀번호 유효성 검사 실패 시 회원가입은 실패한다.")
      void should_failToSignUp_when_passwordValidateNotPass(String invalidPassword)
          throws Exception {
        mockingValidResponse(null);
        params.set("password", invalidPassword);
        callSignUpApi(thumbnail)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                .value(ExceptionAdvice.getErrorMessage(invalidPassword, "password",
                    AuthFieldError.PASSWORD_INVALID.getMessage())));
      }

      @ParameterizedTest
      @ValueSource(strings = {"", "abcde###", "oh-my-zsh", "한글을_사랑합시다", "cant blank",
          "01234567890123456", "         ", "012345678901234567890"})
      @DisplayName("닉네임 유효성 검사 실패 시 회원가입은 실패한다.")
      void should_failToSignUp_when_nicknameValidateNotPass(String invalidNickname)
          throws Exception {
        mockingValidResponse(null);
        params.set("nickname", invalidNickname);
        callSignUpApi(thumbnail)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                .value(ExceptionAdvice.getErrorMessage(invalidNickname, "nickname",
                    AuthFieldError.NICKNAME_INVALID.getMessage())));
      }

      @Test
      @DisplayName("아이디가 중복일 경우 회원가입은 실패한다.")
      void should_failToSignUp_when_loginIDDuplicate() throws Exception {
        doThrow(new BusinessException(validLoginId, "loginId", LOGIN_ID_DUPLICATE))
            .when(authService).signUp(any());
        callSignUpApi(thumbnail)
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message")
                .value(ExceptionAdvice.getErrorMessage(validLoginId, "loginId",
                    LOGIN_ID_DUPLICATE.getMessage())));
      }

      @Test
      @DisplayName("닉네임이 중복일 경우 회원가입은 실패한다.")
      void should_failToSignUp_when_nicknameDuplicate() throws Exception {
        doThrow(new BusinessException(validNickname, "nickname", NICKNAME_DUPLICATE))
            .when(authService).signUp(any());
        callSignUpApi(thumbnail)
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message")
                .value(ExceptionAdvice.getErrorMessage(validNickname, "nickname",
                    NICKNAME_DUPLICATE.getMessage())));
      }

      @Test
      @DisplayName("썸네일이 이미지가 아닐 경우 회원가입은 실패한다.")
      void should_failToSignUp_when_thumbnailIsNotImage() throws Exception {
        mockingValidResponse(null);
        callSignUpApi(thumbnailTestHelper.getFakeImageFile())
            .andExpect(status().isBadRequest());
      }
    }
  }

  @Nested
  @DisplayName("로그인")
  class Login {

    private long userId = 1L;
    private String loginId = "helloWorld";
    private String password = "helloFood12";
    private String encodePassword = passwordEncoder.encode(password);
    private User user = User.builder()
        .loginId(loginId)
        .password(encodePassword)
        .build();

    @Test
    @DisplayName("로그인을 성공한다.")
    void should_success_when_login() throws Exception {
      LoginRequestDto loginRequestDto = LoginRequestDto.builder()
          .loginId(loginId)
          .password(password)
          .build();

      String accessToken = "accessToken";
      String refreshToken = "refreshToken";
      doReturn(userId).when(authService).login(any(LoginRequestDto.class));
      doReturn(accessToken).when(tokenService).createAccessToken(anyLong(), any(RoleType.class));
      doReturn(refreshToken).when(tokenService).createRefreshToken(anyLong());
      doReturn(new Cookie(ACCESS_TOKEN.getName(), accessToken))
          .when(cookieUtils)
          .generateCookie(ACCESS_TOKEN.getName(), accessToken,
              (int) ACCESS_TOKEN.getValidMilliSecond() / 1000);
      doReturn(new Cookie(REFRESH_TOKEN.getName(), refreshToken))
          .when(cookieUtils)
          .generateCookie(REFRESH_TOKEN.getName(), refreshToken,
              (int) REFRESH_TOKEN.getValidMilliSecond() / 1000);

      callLoginApi(loginRequestDto)
          .andExpect(status().isOk())
          .andDo(document("log-in",
              requestFields(
                  fieldWithPath("loginId").description("로그인 아이디"),
                  fieldWithPath("password").description("비밀번호")
              ),
              responseCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description("사용자 인증에 필요한 access token"),
                  cookieWithName(REFRESH_TOKEN.getName()).description("인증 토큰 갱신에 필요한 refresh token")
              )));
    }

    @Test
    @DisplayName("로그인 아이디가 존재하지 않으면 로그인은 실패한다.")
    void should_failToLogin_when_loginIdNotExist() throws Exception {
      String loginId = "helloWorld2";
      LoginRequestDto loginRequestDto = LoginRequestDto.builder()
          .loginId(loginId)
          .password(password)
          .build();

      doThrow(new BusinessException(loginId, "loginId", USER_NOT_FOUND))
          .when(authService)
          .login(any(LoginRequestDto.class));

      callLoginApi(loginRequestDto)
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value(
              ExceptionAdvice.getErrorMessage(loginId, "loginId",
                  USER_NOT_FOUND.getMessage())));
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 로그인은 실패한다.")
    void should_failToLogin_when_passwordNotMatch() throws Exception {
      String wrongPassword = password + "3";
      LoginRequestDto loginRequestDto = LoginRequestDto.builder()
          .loginId(loginId)
          .password(wrongPassword)
          .build();

      doThrow(new BusinessException(wrongPassword, "password", PASSWORD_NOT_MATCH))
          .when(authService)
          .login(any(LoginRequestDto.class));

      callLoginApi(loginRequestDto)
          .andExpect(status().isUnauthorized())
          .andExpect(jsonPath("$.message").value(
              ExceptionAdvice.getErrorMessage(wrongPassword, "password",
                  PASSWORD_NOT_MATCH.getMessage())));
    }

    private ResultActions callLoginApi(LoginRequestDto loginRequestDto) throws Exception {
      return mockMvc.perform(post("/log-in")
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(asJsonString(loginRequestDto)))
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("로그아웃")
  class Logout {

    private long userId = 1L;
    private String userToken = jwtTokenProvider.createAccessToken(userId, RoleType.ROLE_회원);

    @Test
    @DisplayName("로그아웃을 성공한다.")
    void should_success_when_logout() throws Exception {
      doNothing().when(tokenService).deleteToken(anyLong());

      callLogoutApi()
          .andExpect(status().isNoContent())
          .andDo(document("log-out",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description("사용자 인증에 필요한 access token")
              )));
    }

    @Test
    @DisplayName("로그인을 하지 않은(토큰이 존재하지 않는) 유저는 로그아웃이 실패한다.")
    void should_failToLogout_when_tokenNotExist() throws Exception {
      mockMvc.perform(post("/log-out"))
          .andDo(print())
          .andExpect(status().isUnauthorized());
    }

    private ResultActions callLogoutApi() throws Exception {
      return mockMvc.perform(post("/log-out")
              .cookie(new Cookie(ACCESS_TOKEN.getName(), userToken)))
          .andDo(print());
    }
  }
}
