package com.dinosaur.foodbowl.domain.follow.api;

import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.ACCESS_TOKEN;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider.ACCESS_TOKEN_VALID_MILLISECOND;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
      doReturn(me).when(authUtil).getUserByJWT();
      doReturn(myId).when(authUtil).getUserIdByJWT();
      doReturn(other).when(userFindDao).findById(otherId);
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
                          + "만료 시간: " + ACCESS_TOKEN_VALID_MILLISECOND / 1000 + "초")
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
                          + "만료 시간: " + ACCESS_TOKEN_VALID_MILLISECOND / 1000 + "초")
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
}
