package com.dinosaur.foodbowl.domain.user.api;

import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_INTRODUCE_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_LOGIN_ID_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_NICKNAME_LENGTH;
import static com.dinosaur.foodbowl.domain.user.exception.UserErrorCode.LOGIN_ID_DUPLICATE;
import static com.dinosaur.foodbowl.domain.user.exception.UserErrorCode.NICKNAME_DUPLICATE;
import static com.dinosaur.foodbowl.global.config.security.JwtTokenProvider.ACCESS_TOKEN;
import static com.dinosaur.foodbowl.global.config.security.JwtTokenProvider.DEFAULT_TOKEN_VALID_MILLISECOND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.application.DeleteAccountService;
import com.dinosaur.foodbowl.domain.user.application.GetProfileService;
import com.dinosaur.foodbowl.domain.user.application.UpdateProfileService;
import com.dinosaur.foodbowl.domain.user.application.signup.SignUpService;
import com.dinosaur.foodbowl.domain.user.dto.request.SignUpRequestDto;
import com.dinosaur.foodbowl.domain.user.dto.response.ProfileResponseDto;
import com.dinosaur.foodbowl.domain.user.dto.response.SignUpResponseDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType;
import com.dinosaur.foodbowl.domain.user.exception.UserException;
import com.dinosaur.foodbowl.domain.user.exception.UserExceptionAdvice;
import com.dinosaur.foodbowl.global.api.ControllerTest;
import com.dinosaur.foodbowl.global.util.auth.AuthUtil;
import jakarta.servlet.http.Cookie;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@WebMvcTest(UserController.class)
class UserControllerTest extends ControllerTest {

  @MockBean
  private SignUpService signUpService;

  @MockBean
  DeleteAccountService deleteAccountService;

  @MockBean
  UpdateProfileService updateProfileService;

  @MockBean
  GetProfileService getProfileService;

  @MockBean
  AuthUtil authUtil;

  @Nested
  @DisplayName("회원가입")
  class SignUp {

    private final Long userId = 1L;
    private final String userToken = jwtTokenProvider.createAccessToken(userId, RoleType.ROLE_회원);
    private final String validLoginId = "LoginId_123";
    private final String validPassword = "Password123";
    private final String validNickname = "바보gusah009";
    private final String validIntroduce = "Introduce";

    private MockMultipartFile thumbnail;
    private MultiValueMap<String, String> params;

    @BeforeEach
    void setUpSignUp() throws IOException {
      thumbnail = getThumbnailFile();
      params = new LinkedMultiValueMap<>();
      params.add("loginId", validLoginId);
      params.add("password", validPassword);
      params.add("nickname", validNickname);
      params.add("introduce", validIntroduce);
    }

    private ResultActions callSignUpApiWithoutThumbnail() throws Exception {
      return mockMvc.perform(multipart("/users/sign-up")
              .params(params)
              .contentType(MediaType.MULTIPART_FORM_DATA)
              .with(request -> {
                request.setMethod("POST");
                return request;
              }))
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

      when(signUpService.signUp(any())).thenReturn(responseDto);
      when(authUtil.getUserByJWT()).thenReturn(user);
    }

    private ResultActions callSignUpApi(MockMultipartFile thumbnail) throws Exception {
      return mockMvc.perform(multipart("/users/sign-up")
              .file(thumbnail)
              .queryParams(params)
              .contentType(MediaType.MULTIPART_FORM_DATA)
              .with(request -> {
                request.setMethod("POST");
                return request;
              }))
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

      when(signUpService.signUp(any())).thenReturn(responseDto);
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
                    cookieWithName(ACCESS_TOKEN).description("사용자 인증에 필요한 access token")
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
                    SignUpRequestDto.LOGIN_ID_INVALID)));
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
                    SignUpRequestDto.PASSWORD_INVALID)));
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
                    SignUpRequestDto.NICKNAME_INVALID)));
      }

      @Test
      @DisplayName("아이디가 중복일 경우 회원가입은 실패한다.")
      void should_returnBadRequest_when_duplicateLoginId() throws Exception {
        when(signUpService.signUp(any()))
            .thenThrow(new UserException(validLoginId, "loginId", LOGIN_ID_DUPLICATE));
        callSignUpApi(thumbnail)
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message")
                .value(UserExceptionAdvice.getErrorMessage(validLoginId, "loginId",
                    LOGIN_ID_DUPLICATE.getMessage())));
      }

      @Test
      @DisplayName("닉네임이 중복일 경우 회원가입은 실패한다.")
      void should_returnBadRequest_when_duplicateNickname() throws Exception {
        when(signUpService.signUp(any()))
            .thenThrow(new UserException(validNickname, "nickname", NICKNAME_DUPLICATE));
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
        callSignUpApi(getFakeImageFile())
            .andExpect(status().isBadRequest());
      }
    }
  }

  @Nested
  @DisplayName("회원 탈퇴")
  class deleteAccount {

    private final Long userId = 1L;
    private final String userToken = jwtTokenProvider.createAccessToken(userId, RoleType.ROLE_회원);

    @BeforeEach
    void setup() {
      User user = User.builder().build();
      ReflectionTestUtils.setField(user, "id", userId);
      when(authUtil.getUserByJWT()).thenReturn(user);
    }

    @Test
    @DisplayName("본인의 JWT로 회원 탈퇴는 성공한다.")
    void should_deleteSuccessfully_when_deleteMySelf() throws Exception {
      doNothing().when(deleteAccountService).deleteMySelf(any());
      mockMvc.perform(delete("/users")
              .cookie(new Cookie(ACCESS_TOKEN, userToken)))
          .andExpect(status().isNoContent())
          .andDo(print())
          .andDo(document("user-delete",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN).description(
                      "로그인이나 회원가입 시 얻을 수 있는 접근 토큰입니다. \n\n"
                          + "만료 시간: " + DEFAULT_TOKEN_VALID_MILLISECOND / 1000 + "초")
              )));
    }

    @Test
    @DisplayName("토큰이 없을 경우 회원 탈퇴는 실패한다.")
    void should_deleteFailed_when_noToken() throws Exception {
      mockMvc.perform(delete("/users"))
          .andExpect(status().isUnauthorized())
          .andDo(print());
    }

    @Test
    @DisplayName("잘못된 토큰으로 회원 탈퇴는 실패한다.")
    void should_deleteFailed_when_invalidToken() throws Exception {
      mockMvc.perform(delete("/users")
              .cookie(new Cookie(ACCESS_TOKEN, userToken + "haha")))
          .andExpect(status().isUnauthorized())
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("프로필 수정")
  class UpdateProfile {

    private final Long userId = 1L;
    private final String userToken = jwtTokenProvider.createAccessToken(userId, RoleType.ROLE_회원);
    private final String validIntroduce = "Introduce";

    private MockMultipartFile thumbnail;
    private MultiValueMap<String, String> params;

    @BeforeEach
    void setup() throws IOException {
      thumbnail = getThumbnailFile();
      params = new LinkedMultiValueMap<>();
      params.add("introduce", validIntroduce);
    }

    @Test
    @DisplayName("썸네일과 소개글 모두 포함되어 있어도 프로필 수정은 성공한다.")
    void should_successfully_when_validRequest() throws Exception {
      mockUpdateProfileService();
      callUpdateProfileApi(thumbnail)
          .andExpect(status().isNoContent())
          .andExpect(header().string("location", "/users/" + userId))
          .andDo(document("update-profile",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN).description(
                      "로그인이나 회원가입 시 얻을 수 있는 접근 토큰입니다. \n\n"
                          + "만료 시간: " + DEFAULT_TOKEN_VALID_MILLISECOND / 1000 + "초")
              ),
              queryParameters(
                  parameterWithName("introduce")
                      .description("수정할 유저 소개 (최대 가능 길이 :" + MAX_INTRODUCE_LENGTH)
                      .optional()
              ),
              requestParts(
                  partWithName("thumbnail").description("유저가 수정할 썸네일")
                      .optional()
              )));
    }

    @Test
    @DisplayName("수정할 썸네일이 없어도 프로필 수정은 성공한다.")
    void should_successfully_when_nullThumbnail() throws Exception {
      mockUpdateProfileService();
      callUpdateProfileApiWithoutThumbnail()
          .andExpect(status().isNoContent())
          .andExpect(header().string("location", "/users/" + userId));
    }

    @Test
    @DisplayName("수정할 소개글이 없어도 프로필 수정은 성공한다.")
    void should_successfully_when_nullIntroduce() throws Exception {
      mockUpdateProfileService();
      callUpdateProfileApi(thumbnail)
          .andExpect(status().isNoContent())
          .andExpect(header().string("location", "/users/" + userId));
    }

    @Test
    @DisplayName("소개글이나 썸네일이 없어도 프로필 수정은 성공한다.")
    void should_successfully_when_nullEverything() throws Exception {
      params.set("introduce", null);
      mockUpdateProfileService();
      callUpdateProfileApiWithoutThumbnail()
          .andExpect(status().isNoContent())
          .andExpect(header().string("location", "/users/" + userId));
    }

    @Test
    @DisplayName("소개글이 너무 길 경우 프로필 수정은 실패한다.")
    void should_400BadRequest_when_tooLongIntroduce() throws Exception {
      params.set("introduce", "a".repeat(MAX_INTRODUCE_LENGTH + 1));
      mockUpdateProfileService();
      callUpdateProfileApi(thumbnail)
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("썸네일이 이미지가 아닐 경우 프로필 수정은 실패한다.")
    void should_400BadRequest_when_thumbnailIsNotImage() throws Exception {
      mockUpdateProfileService();
      callUpdateProfileApi(getFakeImageFile())
          .andExpect(status().isBadRequest());
    }

    private void mockUpdateProfileService() {
      when(updateProfileService.updateProfile(any(), any())).thenReturn(userId);
    }

    private ResultActions callUpdateProfileApi(MockMultipartFile thumbnail) throws Exception {
      return mockMvc.perform(multipart("/users")
          .file(thumbnail)
          .cookie(new Cookie(ACCESS_TOKEN, userToken))
          .params(params)
          .header("Authorization", userToken)
          .queryParams(params)
          .contentType(MediaType.MULTIPART_FORM_DATA)
          .with(request -> {
            request.setMethod("PATCH");
            return request;
          }));
    }

    private ResultActions callUpdateProfileApiWithoutThumbnail() throws Exception {
      return mockMvc.perform(multipart("/users")
          .cookie(new Cookie(ACCESS_TOKEN, userToken))
          .params(params)
          .contentType(MediaType.MULTIPART_FORM_DATA)
          .with(request -> {
            request.setMethod("PATCH");
            return request;
          }));
    }
  }

  @Nested
  @DisplayName("프로필 가져오기")
  class GetProfile {

    private final Long userId = 1L;
    private final String validNickname = "바보gusah009";
    private final String validIntroduce = "Introduce";
    private final String thumbnailURL = "/hello/world/haha.jpg";
    private final long followerCount = 0;
    private final long followingCount = 0;
    private final long postCount = 10;
    private final String userToken = jwtTokenProvider.createAccessToken(userId, RoleType.ROLE_회원);

    @Test
    @DisplayName("유효한 유저 아이디의 프로필 가져오기는 성공한다.")
    void should_successfully_when_validUserId() throws Exception {
      mockingDto();
      mockMvc.perform(get("/users/{userId}", userId)
              .cookie(new Cookie(ACCESS_TOKEN, userToken)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.userId").value(userId))
          .andExpect(jsonPath("$.nickname").value(validNickname))
          .andExpect(jsonPath("$.introduce").value(validIntroduce))
          .andExpect(jsonPath("$.followerCount").value(followerCount))
          .andExpect(jsonPath("$.followingCount").value(followingCount))
          .andExpect(jsonPath("$.postCount").value(postCount))
          .andExpect(jsonPath("$.thumbnailURL").value(thumbnailURL))
          .andDo(print())
          .andDo(document("get-profile",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN).description(
                      "로그인이나 회원가입 시 얻을 수 있는 접근 토큰입니다. \n\n"
                          + "만료 시간: " + DEFAULT_TOKEN_VALID_MILLISECOND / 1000 + "초")
              ),
              pathParameters(
                  parameterWithName("userId").description("유저의 아이디")
              ),
              responseFields(
                  fieldWithPath("userId").description("DB에 저장된 user의 고유 ID 값"),
                  fieldWithPath("nickname").description("저장된 닉네임"),
                  fieldWithPath("introduce").description("저장된 소개글"),
                  fieldWithPath("followerCount").description("유저의 팔로워 수"),
                  fieldWithPath("followingCount").description("유저의 팔로잉 수"),
                  fieldWithPath("postCount").description("유저의 게시글 수"),
                  fieldWithPath("thumbnailURL").description(
                      "썸네일 URL. 서버 URL 뒤에 그대로 붙이면 파일을 얻을 수 있음.")
              )));
    }

    @Test
    @DisplayName("썸네일이 없어도 유저 아이디의 프로필 가져오기는 성공한다.")
    void should_successfully_when_thumbnailIsNull() throws Exception {
      mockingDtoWithoutThumbnail();
      mockMvc.perform(get("/users/" + userId)
              .cookie(new Cookie(ACCESS_TOKEN, userToken)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.userId").value(userId))
          .andExpect(jsonPath("$.nickname").value(validNickname))
          .andExpect(jsonPath("$.introduce").value(validIntroduce))
          .andExpect(jsonPath("$.followerCount").value(followerCount))
          .andExpect(jsonPath("$.followingCount").value(followingCount))
          .andExpect(jsonPath("$.postCount").value(postCount))
          .andExpect(jsonPath("$.thumbnailURL").isEmpty())
          .andDo(print());
    }

    private void mockingDtoWithoutThumbnail() {
      ProfileResponseDto user = ProfileResponseDto.builder()
          .userId(userId)
          .nickname(validNickname)
          .introduce(validIntroduce)
          .followerCount(followerCount)
          .followingCount(followingCount)
          .postCount(postCount)
          .build();
      when(getProfileService.getProfile(userId)).thenReturn(user);
    }

    private void mockingDto() {
      ProfileResponseDto user = ProfileResponseDto.builder()
          .userId(userId)
          .nickname(validNickname)
          .introduce(validIntroduce)
          .followerCount(followerCount)
          .followingCount(followingCount)
          .postCount(postCount)
          .thumbnailURL(thumbnailURL)
          .build();
      when(getProfileService.getProfile(userId)).thenReturn(user);
    }
  }

  private MockMultipartFile getThumbnailFile() throws IOException {
    return new MockMultipartFile("thumbnail",
        "testImage_210x210.png", "image/png",
        new FileInputStream("src/test/resources/images/testImage_1x1.png"));
  }

  private MockMultipartFile getFakeImageFile() throws IOException {
    return new MockMultipartFile("thumbnail",
        "fakeImage.png", "image/png",
        new FileInputStream("src/test/resources/images/fakeImage.png"));
  }
}