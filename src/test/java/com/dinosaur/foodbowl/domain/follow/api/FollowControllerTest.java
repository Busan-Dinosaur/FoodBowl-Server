package com.dinosaur.foodbowl.domain.follow.api;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dinosaur.foodbowl.domain.follow.application.FollowService;
import com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType;
import com.dinosaur.foodbowl.global.api.ControllerTest;
import com.dinosaur.foodbowl.global.util.auth.AuthUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
          .andDo(print());
    }

    @Test
    @DisplayName("언팔로우를 성공하면 204")
    void shouldSucceedUnfollowWhenValidatedUsers() throws Exception {
      callFollowApi().andExpect(status().isNoContent())
          .andDo(print());
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
          .header("Authorization", userToken)
          .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions callUnfollowApi() throws Exception {
      return mockMvc.perform(delete("/follows/{userId}", otherId)
          .header("Authorization", userToken)
          .contentType(MediaType.APPLICATION_JSON));
    }

  }
}