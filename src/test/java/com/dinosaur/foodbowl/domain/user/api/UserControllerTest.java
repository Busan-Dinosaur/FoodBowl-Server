package com.dinosaur.foodbowl.domain.user.api;

import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_INTRODUCE_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_LOGIN_ID_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_NICKNAME_LENGTH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.application.DeleteAccountService;
import com.dinosaur.foodbowl.domain.user.application.signup.SignUpService;
import com.dinosaur.foodbowl.domain.user.dto.response.SignUpResponseDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType;
import com.dinosaur.foodbowl.global.api.ControllerTest;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@WebMvcTest(UserController.class)
class UserControllerTest extends ControllerTest {

  @MockBean
  private SignUpService signUpService;

  @MockBean
  DeleteAccountService deleteAccountService;

  @Nested
  @DisplayName("회원가입")
  class SignUp {

    private final Long userId = 1L;
    private final String userToken = jwtTokenProvider.createAccessToken(userId, RoleType.ROLE_회원);

    private MockMultipartFile thumbnail;
    private MultiValueMap<String, String> params;

    @BeforeEach
    void setUpSignUp() throws IOException {
      thumbnail = getThumbnailFile();
      params = new LinkedMultiValueMap<>();
      params.add("loginId", "loginId");
      params.add("password", "password");
      params.add("nickname", "nickname");
      params.add("introduce", "introduce");
    }

    private ResultActions callSignUpApiWithoutThumbnail() throws Exception {
      when(signUpService.signUp(any())).thenReturn(SignUpResponseDto.of(
          userId,
          User.builder()
              .loginId("loginId")
              .password("password")
              .nickname("nickname")
              .introduce("introduce")
              .build(),
          userToken));
      return mockMvc.perform(multipart("/users/sign-up")
              .params(params)
              .contentType(MediaType.MULTIPART_FORM_DATA)
              .with(request -> {
                request.setMethod("POST");
                return request;
              }))
          .andDo(print());
    }

    private ResultActions callSignUpApi() throws Exception {
      when(signUpService.signUp(any())).thenReturn(SignUpResponseDto.of(
          userId,
          User.builder()
              .loginId("loginId")
              .password("password")
              .nickname("nickname")
              .introduce("introduce")
              .thumbnail(Thumbnail.builder()
                  .height(200)
                  .width(200)
                  .path("/thumbnail/2022-01-11/random_name.jpeg")
                  .build())
              .build(),
          userToken));
      return mockMvc.perform(multipart("/users/sign-up")
              .file(thumbnail)
              .params(params)
              .contentType(MediaType.MULTIPART_FORM_DATA)
              .with(request -> {
                request.setMethod("POST");
                return request;
              }))
          .andDo(print());
    }

    private MockMultipartFile getThumbnailFile() throws IOException {
      return new MockMultipartFile("thumbnail",
          "testImage_210x210.png", "image/png",
          new FileInputStream("src/test/resources/images/testImage_1x1.png"));
    }

    @Nested
    @DisplayName("회원가입 성공")
    class SignUpSuccess {

      @Test
      @DisplayName("썸네일이 있을 경우 회원가입은 성공한다.")
      void should_successfully_when_validRequest() throws Exception {
        callSignUpApi()
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").exists())
            .andExpect(jsonPath("$.loginId").value("loginId"))
            .andExpect(jsonPath("$.nickname").value("nickname"))
            .andExpect(jsonPath("$.introduce").value("introduce"))
            .andExpect(jsonPath("$.thumbnailURL").exists())
            .andExpect(jsonPath("$.accessToken").exists())
            .andDo(document("sign-up",
                requestParameters(
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
                        .description("썸네일 URL. 서버 URL 뒤에 그대로 붙이면 파일을 얻을 수 있음."),
                    fieldWithPath("accessToken")
                        .description("사용자 인증에 필요한 access token.\r\n"
                            + " API 호출 시 Authorization 헤더에 넣어서 보내주면 됨")
                )));
      }

      @Test
      @DisplayName("썸네일이 없어도 회원가입은 성공한다.")
      void should_returnIsOK_when_thumbnailIsNull() throws Exception {
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
        params.set("loginId", "a".repeat(MAX_LOGIN_ID_LENGTH + 1));
        callSignUpApi().andExpect(status().isBadRequest());
        params.set("loginId", "loginId");

        params.set("nickname", "a".repeat(MAX_NICKNAME_LENGTH + 1));
        callSignUpApi().andExpect(status().isBadRequest());
        params.set("nickname", "nickname");

        params.set("introduce", "a".repeat(MAX_INTRODUCE_LENGTH + 1));
        callSignUpApi().andExpect(status().isBadRequest());
        params.set("introduce", "introduce");
      }
    }
  }

  @Nested
  @DisplayName("회원 탈퇴")
  class deleteAccount {

    private final Long userId = 1L;
    private final String userToken = jwtTokenProvider.createAccessToken(userId, RoleType.ROLE_회원);

    @Test
    @DisplayName("본인의 JWT로 회원 탈퇴는 성공한다.")
    void should_deleteSuccessfully_when_deleteMySelf() throws Exception {
      doNothing().when(deleteAccountService).deleteMySelf();
      mockMvc.perform(delete("/users")
              .header("Authorization", userToken))
          .andExpect(status().isNoContent())
          .andDo(print())
          .andDo(document("user-delete"));
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
              .header("Authorization", userToken + "haha"))
          .andExpect(status().isUnauthorized())
          .andDo(print());
    }
  }
}