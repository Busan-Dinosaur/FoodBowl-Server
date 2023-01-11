package com.dinosaur.foodbowl.domain.user.api;

import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_INTRODUCE_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_LOGIN_ID_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_NICKNAME_LENGTH;
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

import com.dinosaur.foodbowl.domain.user.dao.UserRepository;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType;
import com.dinosaur.foodbowl.global.api.IntegrationTest;
import com.dinosaur.foodbowl.global.config.security.JwtTokenProvider;
import com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailUtil;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class UserControllerTest extends IntegrationTest {

  @Autowired
  ThumbnailUtil thumbnailUtil;

  @Autowired
  UserRepository userRepository;

  @Autowired
  JwtTokenProvider jwtTokenProvider;

  @Nested
  class SignUp {

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
    class SignUpSuccess {

      @Test
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
      void should_returnIsOK_when_thumbnailIsNull() throws Exception {
        callSignUpApiWithoutThumbnail()
            .andExpect(status().isCreated());
      }
    }

    @Nested
    class SignUpValidation {

      @Test
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
  class deleteAccount {

    private User user;
    private String userToken;

    @BeforeEach
    void setupUser() throws IOException {
      User userWithThumbnail = User.builder()
          .loginId("loginId")
          .nickname("nickname")
          .password("password")
          .introduce("introduce")
          .thumbnail(thumbnailUtil.save(getThumbnailFile()))
          .build();
      user = userRepository.save(userWithThumbnail);
      userToken = jwtTokenProvider.createAccessToken(user.getId(), RoleType.ROLE_회원);
    }

    private MockMultipartFile getThumbnailFile() throws IOException {
      return new MockMultipartFile("thumbnail",
          "testImage_1x1.png", "image/png",
          new FileInputStream("src/test/resources/images/testImage_1x1.png"));
    }

    @Test
    void should_deleteSuccessfully_when_deleteMySelf() throws Exception {
      mockMvc.perform(delete("/users")
              .header("Authorization", userToken))
          .andExpect(status().isNoContent())
          .andDo(print())
          .andDo(document("user-delete"));
    }

    @Test
    void should_deleteFailed_when_noToken() throws Exception {
      mockMvc.perform(delete("/users"))
          .andExpect(status().isUnauthorized())
          .andDo(print());
    }

    @Test
    void should_deleteFailed_when_invalidToken() throws Exception {
      mockMvc.perform(delete("/users")
              .header("Authorization", userToken + "haha"))
          .andExpect(status().isUnauthorized())
          .andDo(print());
    }
  }
}