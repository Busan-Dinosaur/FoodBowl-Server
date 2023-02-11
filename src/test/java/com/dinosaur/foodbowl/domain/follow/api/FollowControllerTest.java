package com.dinosaur.foodbowl.domain.follow.api;

import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.ACCESS_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.follow.dto.FollowGetResponseDto;
import com.dinosaur.foodbowl.domain.user.entity.Role.RoleType;
import com.dinosaur.foodbowl.domain.user.entity.User;
import jakarta.servlet.http.Cookie;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;

public class FollowControllerTest extends IntegrationTest {

  @Nested
  @DisplayName("팔로우 & 언팔로우")
  class FollowAndUnfollow {

    private final Long myId = 1L;
    private final Long otherId = 2L;
    private final String userToken = jwtTokenProvider.createAccessToken(myId, RoleType.ROLE_회원);

    @BeforeEach
    void setup() {
      User me = User.builder().build();
      User other = User.builder().build();
      ReflectionTestUtils.setField(me, "id", myId);
      doReturn(me).when(userFindService).findById(anyLong());
      doReturn(other).when(userFindService).findById(otherId);
    }

    @Test
    @DisplayName("팔로우 요청을 성공하면 204 NO Content 를 반환한다.")
    void shouldSucceedFollowWhenValidatedUsers() throws Exception {
      callFollowApi(otherId).andExpect(status().isNoContent())
          .andDo(print())
          .andDo(document("follow",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description(
                      "로그인이나 회원가입 시 얻을 수 있는 접근 토큰입니다. \n\n"
                          + "만료 시간: " + ACCESS_TOKEN.getValidMilliSecond() / 1000 + "초")
              ),
              pathParameters(
                  parameterWithName("userId").description("팔로우할 유저의 아이디")
              )));
    }

    @Test
    @DisplayName("팔로우 취소 요청을 성공하면 204 NO Content 를 반환한다.")
    void shouldSucceedUnfollowWhenValidatedUsers() throws Exception {
      callUnfollowApi(otherId).andExpect(status().isNoContent())
          .andDo(print())
          .andDo(document("unfollow",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description(
                      "로그인이나 회원가입 시 얻을 수 있는 접근 토큰입니다. \n\n"
                          + "만료 시간: " + ACCESS_TOKEN.getValidMilliSecond() / 1000 + "초")
              ),
              pathParameters(
                  parameterWithName("userId").description("팔로우를 취소할 유저의 아이디")
              )));
    }

    @Test
    @DisplayName("자신에 대한 팔로우 요청은 400 Bad Request 를 반환한다.")
    void shouldClientErrorWhenFollowOneself() throws Exception {
      callFollowApi(myId).andExpect(status().isBadRequest())
          .andDo(print());
    }

    @Test
    @DisplayName("자신에 대한 팔로우 취소 요청은 400 Bad Request 를 반환한다.")
    void shouldClientErrorWhenUnfollowOneself() throws Exception {
      callUnfollowApi(myId).andExpect(status().isBadRequest())
          .andDo(print());

    }


    @Test
    @DisplayName("토큰이 없을 경우 팔로우는 실패한다.")
    void shouldFailFollowWhenNoToken() throws Exception {
      mockMvc.perform(post("/follows/{userId}", otherId))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("토큰이 없을 경우 언팔로우는 실패한다.")
    void shouldFailUnfollowWhenNoToken() throws Exception {
      mockMvc.perform(delete("/follows/{userId}", otherId))
          .andExpect(status().isUnauthorized());
    }


    private ResultActions callFollowApi(Long userId) throws Exception {
      return mockMvc.perform(post("/follows/{userId}", userId)
          .cookie(new Cookie(ACCESS_TOKEN.getName(), userToken))
          .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions callUnfollowApi(Long userId) throws Exception {
      return mockMvc.perform(delete("/follows/{userId}", userId)
          .cookie(new Cookie(ACCESS_TOKEN.getName(), userToken))
          .contentType(MediaType.APPLICATION_JSON));
    }

  }

  @Nested
  @DisplayName("팔로우 & 팔로잉한 유저 리스트 조회")
  class GetFollow {

    @Test
    @DisplayName("유저가 팔로우한 유저리스트를 조회를 성공한다")
    void should_success_get_followings() throws Exception {
      mockingAuth();

      FollowGetResponseDto response1 = FollowGetResponseDto.builder()
          .userId(2L)
          .thumbnailURL("url1")
          .nickName("following1")
          .build();
      FollowGetResponseDto response2 = FollowGetResponseDto.builder()
          .userId(3L)
          .thumbnailURL("url2")
          .nickName("following2")
          .build();

      doReturn(List.of(response1, response2)).when(getFollowService)
          .getFollowings(any(User.class), any(Pageable.class));

      callGetFollowingsApi("1")
          .andExpect(status().isOk())
          .andExpect(jsonPath("[0].userId").value(response1.getUserId()))
          .andExpect(jsonPath("[0].nickName").value(response1.getNickName()))
          .andExpect(jsonPath("[0].thumbnailURL").value(response1.getThumbnailURL()))
          .andExpect(jsonPath("[1].userId").value(response2.getUserId()))
          .andExpect(jsonPath("[1].nickName").value(response2.getNickName()))
          .andExpect(jsonPath("[1].thumbnailURL").value(response2.getThumbnailURL()))
          .andDo(document("follow-get-followers",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description("사용자 인증에 필요한 access token")
              ),
              pathParameters(
                  parameterWithName("id").description("팔로워 목록을 조회할 유저 ID")
              ),
              queryParameters(
                  parameterWithName("pages").optional()
                      .description("불러오고 싶은 팔로워 목록 페이지 +\n(default: 0)"),
                  parameterWithName("size").optional()
                      .description("불러오고 싶은 팔로워 목록 크기 +\n(default: 10)")
              ),
              responseFields(
                  fieldWithPath("[].userId").description("팔로워 ID"),
                  fieldWithPath("[].nickName").description("팔로워 nickname"),
                  fieldWithPath("[].thumbnailURL").description("팔로워 thumbnailURL")
              )));
    }

    private ResultActions callGetFollowingsApi(String userId) throws Exception {
      return mockMvc.perform(get("/follows/{id}/followings", userId)
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "token"))
              .param("pages", "0")
              .param("size", "2"))
          .andDo(print());
    }

    @Test
    @DisplayName("유저를 팔로우한 유저리스트를 조회를 성공한다")
    void should_success_get_followers() throws Exception {
      mockingAuth();

      FollowGetResponseDto response1 = FollowGetResponseDto.builder()
          .userId(2L)
          .thumbnailURL("url1")
          .nickName("follower1")
          .build();
      FollowGetResponseDto response2 = FollowGetResponseDto.builder()
          .userId(3L)
          .thumbnailURL("url2")
          .nickName("follower2")
          .build();

      doReturn(List.of(response1, response2)).when(getFollowService)
          .getFollowers(any(User.class), any(Pageable.class));

      callGetFollowersApi("1")
          .andExpect(status().isOk())
          .andExpect(jsonPath("[0].userId").value(response1.getUserId()))
          .andExpect(jsonPath("[0].nickName").value(response1.getNickName()))
          .andExpect(jsonPath("[0].thumbnailURL").value(response1.getThumbnailURL()))
          .andExpect(jsonPath("[1].userId").value(response2.getUserId()))
          .andExpect(jsonPath("[1].nickName").value(response2.getNickName()))
          .andExpect(jsonPath("[1].thumbnailURL").value(response2.getThumbnailURL()))
          .andDo(document("follow-get-followers",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description("사용자 인증에 필요한 access token")
              ),
              pathParameters(
                  parameterWithName("id").description("팔로워 목록을 조회할 유저 ID")
              ),
              queryParameters(
                  parameterWithName("pages").optional()
                      .description("불러오고 싶은 팔로워 목록 페이지 +\n(default: 0)"),
                  parameterWithName("size").optional()
                      .description("불러오고 싶은 팔로워 목록 크기 +\n(default: 10)")
              ),
              responseFields(
                  fieldWithPath("[].userId").description("팔로워 ID"),
                  fieldWithPath("[].nickName").description("팔로워 nickname"),
                  fieldWithPath("[].thumbnailURL").description("팔로워 thumbnailURL")
              )));
    }

    private ResultActions callGetFollowersApi(String userId) throws Exception {
      return mockMvc.perform(get("/follows/{id}/followers", userId)
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "token"))
              .param("pages", "0")
              .param("size", "2"))
          .andDo(print());
    }

  }
}
