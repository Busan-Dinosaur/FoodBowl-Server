package com.dinosaur.foodbowl.domain.auth.api;

import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_INTRODUCE_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_LOGIN_ID_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_NICKNAME_LENGTH;
import static com.dinosaur.foodbowl.domain.user.exception.UserErrorCode.LOGIN_ID_DUPLICATE;
import static com.dinosaur.foodbowl.domain.user.exception.UserErrorCode.NICKNAME_DUPLICATE;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider.ACCESS_TOKEN;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider.REFRESH_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.auth.dto.AuthFieldError;
import com.dinosaur.foodbowl.domain.auth.dto.request.SignUpRequestDto;
import com.dinosaur.foodbowl.domain.auth.dto.response.SignUpResponseDto;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.domain.user.exception.UserException;
import com.dinosaur.foodbowl.domain.user.exception.UserExceptionAdvice;
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

    private void mockingValidResponseWithoutThumbnail() {
      User user = User.builder()
          .loginId(validLoginId)
          .password(validPassword)
          .nickname(validNickname)
          .introduce(validIntroduce)
          .build();
      ReflectionTestUtils.setField(user, "id", userId);
      SignUpResponseDto responseDto = SignUpResponseDto.of(user);
      ReflectionTestUtils.setField(responseDto, "userId", userId);

      doReturn(user).when(authUtil).getUserByJWT();
      doReturn(responseDto).when(authService).signUp(any(SignUpRequestDto.class));
      doNothing().when(tokenService).saveToken(anyLong(), anyString());
    }

    private ResultActions callSignUpApi(MockMultipartFile thumbnail) throws Exception {
      return mockMvc.perform(multipart(HttpMethod.POST, "/sign-up")
              .file(thumbnail)
              .queryParams(params)
              .contentType(MediaType.MULTIPART_FORM_DATA))
          .andDo(print());
    }

    private void mockingValidResponse() {
      SignUpResponseDto responseDto = SignUpResponseDto.of(
          User.builder()
              .loginId(validLoginId)
              .password(validPassword)
              .nickname(validNickname)
              .introduce(validIntroduce)
              .thumbnail(Thumbnail.builder()
                  .height(200)
                  .width(200)
                  .path("/thumbnail/2022-01-11/random_name.jpeg")
                  .build())
              .build());
      ReflectionTestUtils.setField(responseDto, "userId", userId);

      doReturn(responseDto).when(authService).signUp(any(SignUpRequestDto.class));
      doNothing().when(tokenService).saveToken(anyLong(), anyString());
    }

    @Nested
    @DisplayName("회원가입 성공")
    class SignUpSuccess {

      @Test
      @DisplayName("썸네일이 있을 경우 회원가입은 성공한다.")
      void should_successfully_when_validRequest() throws Exception {
        mockingValidResponse();
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
                    cookieWithName(ACCESS_TOKEN).description("사용자 인증에 필요한 access token"),
                    cookieWithName(REFRESH_TOKEN).description("인증 토큰 갱신에 필요한 refresh token")
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
      void should_returnIsOK_when_thumbnailIsNull() throws Exception {
        mockingValidResponseWithoutThumbnail();
        callSignUpApiWithoutThumbnail()
            .andExpect(status().isCreated());
      }
    }

    @Nested
    @DisplayName("회원가입 유효성 검사")
    class SignUpValidation {

      @Test
      @DisplayName("너무 긴 요청 값일 경우 회원가입은 실패한다.")
      void should_returnBadRequest_when_tooLongParameter() throws Exception {
        mockingValidResponse();

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
      void should_returnBadRequest_when_invalidLoginId(String invalidLoginId) throws Exception {
        mockingValidResponse();
        params.set("loginId", invalidLoginId);
        callSignUpApi(thumbnail)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                .value(UserExceptionAdvice.getErrorMessage(invalidLoginId, "loginId",
                    AuthFieldError.LOGIN_ID_INVALID.getMessage())));
      }

      @ParameterizedTest
      @ValueSource(strings = {"aaaaaaa", "0123456789", "onlyEnglish", "###########", "cant blank",
          "         ", "012345678901234567890"})
      @DisplayName("비밀번호 유효성 검사 실패 시 회원가입은 실패한다.")
      void should_returnBadRequest_when_invalidPassword(String invalidPassword) throws Exception {
        mockingValidResponse();
        params.set("password", invalidPassword);
        callSignUpApi(thumbnail)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                .value(UserExceptionAdvice.getErrorMessage(invalidPassword, "password",
                    AuthFieldError.PASSWORD_INVALID.getMessage())));
      }

      @ParameterizedTest
      @ValueSource(strings = {"", "abcde###", "oh-my-zsh", "한글을_사랑합시다", "cant blank",
          "01234567890123456", "         ", "012345678901234567890"})
      @DisplayName("닉네임 유효성 검사 실패 시 회원가입은 실패한다.")
      void should_returnBadRequest_when_invalidNickname(String invalidNickname) throws Exception {
        mockingValidResponse();
        params.set("nickname", invalidNickname);
        callSignUpApi(thumbnail)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                .value(UserExceptionAdvice.getErrorMessage(invalidNickname, "nickname",
                    AuthFieldError.NICKNAME_INVALID.getMessage())));
      }

      @Test
      @DisplayName("아이디가 중복일 경우 회원가입은 실패한다.")
      void should_returnBadRequest_when_duplicateLoginId() throws Exception {
        doThrow(new UserException(validLoginId, "loginId", LOGIN_ID_DUPLICATE))
            .when(authService).signUp(any());
        callSignUpApi(thumbnail)
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message")
                .value(UserExceptionAdvice.getErrorMessage(validLoginId, "loginId",
                    LOGIN_ID_DUPLICATE.getMessage())));
      }

      @Test
      @DisplayName("닉네임이 중복일 경우 회원가입은 실패한다.")
      void should_returnBadRequest_when_duplicateNickname() throws Exception {
        doThrow(new UserException(validNickname, "nickname", NICKNAME_DUPLICATE))
            .when(authService).signUp(any());
        callSignUpApi(thumbnail)
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message")
                .value(UserExceptionAdvice.getErrorMessage(validNickname, "nickname",
                    NICKNAME_DUPLICATE.getMessage())));
      }

      @Test
      @DisplayName("썸네일이 이미지가 아닐 경우 회원가입은 실패한다.")
      void should_returnBadRequest_when_thumbnailIsNotImage() throws Exception {
        mockingValidResponse();
        callSignUpApi(thumbnailTestHelper.getFakeImageFile())
            .andExpect(status().isBadRequest());
      }
    }
  }
}