package com.dinosaur.foodbowl.domain.user.api;

import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_INTRODUCE_LENGTH;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.ACCESS_TOKEN;
import static com.dinosaur.foodbowl.global.error.ErrorCode.USER_NOT_FOUND;
import static com.dinosaur.foodbowl.global.error.ExceptionAdvice.getErrorMessage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
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

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.auth.dto.response.SignUpResponseDto;
import com.dinosaur.foodbowl.domain.user.dto.request.UpdateProfileRequestDto;
import com.dinosaur.foodbowl.domain.user.dto.response.ProfileResponseDto;
import com.dinosaur.foodbowl.domain.user.entity.Role.RoleType;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.error.BusinessException;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class UserControllerTest extends IntegrationTest {

  @Nested
  @DisplayName("회원 탈퇴")
  class deleteAccount {

    private final Long userId = 1L;
    private final String userToken = jwtTokenProvider.createAccessToken(userId, RoleType.ROLE_회원);

    @BeforeEach
    void setup() {
      User user = User.builder().build();
      ReflectionTestUtils.setField(user, "id", userId);
      doReturn(user).when(userFindDao).findById(anyLong());
    }

    @Test
    @DisplayName("본인의 JWT로 회원 탈퇴는 성공한다.")
    void should_deleteSuccessfully_when_deleteMySelf() throws Exception {
      doNothing().when(deleteAccountService).deleteMySelf(any());
      mockMvc.perform(delete("/users")
              .cookie(new Cookie(ACCESS_TOKEN.getName(), userToken)))
          .andExpect(status().isNoContent())
          .andDo(print())
          .andDo(document("user-delete",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description(
                      "로그인이나 회원가입 시 얻을 수 있는 접근 토큰입니다. \n\n"
                          + "만료 시간: " + ACCESS_TOKEN.getValidMilliSecond() / 1000 + "초")
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
              .cookie(new Cookie(ACCESS_TOKEN.getName(), userToken + "haha")))
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
      thumbnail = thumbnailTestHelper.getThumbnailFile();
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
                  cookieWithName(ACCESS_TOKEN.getName()).description(
                      "로그인이나 회원가입 시 얻을 수 있는 접근 토큰입니다. \n\n"
                          + "만료 시간: " + ACCESS_TOKEN.getValidMilliSecond() / 1000 + "초")
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
      callUpdateProfileApi(thumbnailTestHelper.getFakeImageFile())
          .andExpect(status().isBadRequest());
    }

    private void mockUpdateProfileService() {
      User user = User.builder()
          .introduce(validIntroduce)
          .build();
      ReflectionTestUtils.setField(user, "id", userId);
      SignUpResponseDto responseDto = SignUpResponseDto.of(user);
      ReflectionTestUtils.setField(responseDto, "userId", userId);

      doReturn(user).when(userFindDao).findById(anyLong());
      doReturn(userId).when(updateProfileService)
          .updateProfile(any(User.class), any(UpdateProfileRequestDto.class));
    }

    private ResultActions callUpdateProfileApi(MockMultipartFile thumbnail) throws Exception {
      return mockMvc.perform(multipart(HttpMethod.PATCH, "/users")
          .file(thumbnail)
          .cookie(new Cookie(ACCESS_TOKEN.getName(), userToken))
          .queryParams(params)
          .contentType(MediaType.MULTIPART_FORM_DATA));
    }

    private ResultActions callUpdateProfileApiWithoutThumbnail() throws Exception {
      return mockMvc.perform(multipart(HttpMethod.PATCH, "/users")
          .cookie(new Cookie(ACCESS_TOKEN.getName(), userToken))
          .queryParams(params)
          .contentType(MediaType.MULTIPART_FORM_DATA));
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
              .cookie(new Cookie(ACCESS_TOKEN.getName(), userToken)))
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
                  cookieWithName(ACCESS_TOKEN.getName()).description(
                      "로그인이나 회원가입 시 얻을 수 있는 접근 토큰입니다. \n\n"
                          + "만료 시간: " + ACCESS_TOKEN.getValidMilliSecond() / 1000 + "초")
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
              .cookie(new Cookie(ACCESS_TOKEN.getName(), userToken)))
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

    @Test
    @DisplayName("존재하지 않는 유저의 ID일 경우 404 NOT FOUND를 반환한다. ")
    void should_fail_when_notExistUser() throws Exception {
      String notExistUserId = "-1";
      String field = "userId";
      doThrow(new BusinessException(notExistUserId, field, USER_NOT_FOUND))
          .when(getProfileService)
          .getProfile(Long.parseLong(notExistUserId));
      mockMvc.perform(get("/users/" + notExistUserId)
              .cookie(new Cookie(ACCESS_TOKEN.getName(), userToken)))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message")
              .value(getErrorMessage(notExistUserId, field, USER_NOT_FOUND.getMessage())))
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
      doReturn(user).when(getProfileService).getProfile(userId);
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
      doReturn(user).when(getProfileService).getProfile(userId);
    }
  }
}
