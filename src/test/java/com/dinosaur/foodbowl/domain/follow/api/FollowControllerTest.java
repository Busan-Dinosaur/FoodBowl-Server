package com.dinosaur.foodbowl.domain.follow.api;

import static com.dinosaur.foodbowl.global.config.security.JwtTokenProvider.ACCESS_TOKEN;
import static com.dinosaur.foodbowl.global.config.security.JwtTokenProvider.DEFAULT_TOKEN_VALID_MILLISECOND;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dinosaur.foodbowl.domain.follow.application.FollowService;
import com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType;
import com.dinosaur.foodbowl.global.api.ControllerTest;
import com.dinosaur.foodbowl.global.util.auth.AuthUtil;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(FollowController.class)
public class FollowControllerTest extends ControllerTest {

  @MockBean
  AuthUtil authUtil;
  @MockBean
  FollowService followService;

  @Nested
  @DisplayName("팔로우 & 언팔로우")
  class FollowAndUnfollow {

    private final Long userId = 1L;
    private final Long otherId = 2L;
    private final String userToken = jwtTokenProvider.createAccessToken(userId, RoleType.ROLE_회원);

    @Test
    @DisplayName("팔로잉을 성공하면 204")
    void shouldSucceedFollowWhenValidatedUsers() throws Exception {
      callFollowApi().andExpect(status().isNoContent())
          .andDo(print())
          .andDo(document("follow",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN).description(
                      "로그인이나 회원가입 시 얻을 수 있는 접근 토큰입니다. \n\n"
                          + "만료 시간: " + DEFAULT_TOKEN_VALID_MILLISECOND / 1000 + "초")
              ),
              pathParameters(
                  parameterWithName("userId").description("팔로우할 유저의 아이디")
              )));
    }

    @Test
    @DisplayName("언팔로우를 성공하면 204")
    void shouldSucceedUnfollowWhenValidatedUsers() throws Exception {
      callUnfollowApi().andExpect(status().isNoContent())
          .andDo(print())
          .andDo(document("unfollow",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN).description(
                      "로그인이나 회원가입 시 얻을 수 있는 접근 토큰입니다. \n\n"
                          + "만료 시간: " + DEFAULT_TOKEN_VALID_MILLISECOND / 1000 + "초")
              ),
              pathParameters(
                  parameterWithName("userId").description("팔로우를 취소할 유저의 아이디")
              )));
    }

    @Test
    @DisplayName("토큰이 없을 경우 팔로우는 실패한다.")
    void shouldFailFollowWhenNoToken() throws Exception {
      mockMvc.perform(delete("/follows/{userId}", otherId))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("토큰이 없을 경우 언팔로우는 실패한다.")
    void shouldFailUnfollowWhenNoToken() throws Exception {
      mockMvc.perform(delete("/follows/{userId}", otherId))
          .andExpect(status().isUnauthorized());
    }


    private ResultActions callFollowApi() throws Exception {
      return mockMvc.perform(post("/follows/{userId}", otherId)
          .cookie(new Cookie(ACCESS_TOKEN, userToken))
          .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions callUnfollowApi() throws Exception {
      return mockMvc.perform(delete("/follows/{userId}", otherId)
          .cookie(new Cookie(ACCESS_TOKEN, userToken))
          .contentType(MediaType.APPLICATION_JSON));
    }

  }
}