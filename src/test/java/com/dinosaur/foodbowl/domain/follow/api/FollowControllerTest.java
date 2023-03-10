package com.dinosaur.foodbowl.domain.follow.api;

import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.ACCESS_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
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
import com.dinosaur.foodbowl.domain.follow.dto.FollowerResponseDto;
import com.dinosaur.foodbowl.domain.follow.dto.FollowingResponseDto;
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
  @DisplayName("????????? & ????????????")
  class FollowAndUnfollow {

    private final Long myId = 1L;
    private final Long otherId = 2L;
    private final String userToken = jwtTokenProvider.createAccessToken(myId, RoleType.ROLE_??????);

    @BeforeEach
    void setup() {
      User me = User.builder().build();
      User other = User.builder().build();
      ReflectionTestUtils.setField(me, "id", myId);
      doReturn(me).when(userFindService).findById(anyLong());
      doReturn(other).when(userFindService).findById(otherId);
    }

    @Test
    @DisplayName("????????? ????????? ???????????? 204 NO Content ??? ????????????.")
    void shouldSucceedFollowWhenValidatedUsers() throws Exception {
      doNothing().when(followService).follow(any(User.class), any(User.class));

      callFollowApi(otherId).andExpect(status().isNoContent())
          .andDo(print())
          .andDo(document("follow",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description(
                      "??????????????? ???????????? ??? ?????? ??? ?????? ?????? ???????????????. \n\n"
                          + "?????? ??????: " + ACCESS_TOKEN.getValidMilliSecond() / 1000 + "???")
              ),
              pathParameters(
                  parameterWithName("userId").description("???????????? ????????? ?????????")
              )));
    }

    @Test
    @DisplayName("????????? ?????? ????????? ???????????? 204 NO Content ??? ????????????.")
    void shouldSucceedUnfollowWhenValidatedUsers() throws Exception {
      doNothing().when(followService).unfollow(any(User.class), any(User.class));

      callUnfollowApi(otherId).andExpect(status().isNoContent())
          .andDo(print())
          .andDo(document("unfollow",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description(
                      "??????????????? ???????????? ??? ?????? ??? ?????? ?????? ???????????????. \n\n"
                          + "?????? ??????: " + ACCESS_TOKEN.getValidMilliSecond() / 1000 + "???")
              ),
              pathParameters(
                  parameterWithName("userId").description("???????????? ????????? ????????? ?????????")
              )));
    }

    @Test
    @DisplayName("????????? ?????? ????????? ????????? 400 Bad Request ??? ????????????.")
    void shouldClientErrorWhenFollowOneself() throws Exception {
      callFollowApi(myId).andExpect(status().isBadRequest())
          .andDo(print());
    }

    @Test
    @DisplayName("????????? ?????? ????????? ?????? ????????? 400 Bad Request ??? ????????????.")
    void shouldClientErrorWhenUnfollowOneself() throws Exception {
      callUnfollowApi(myId).andExpect(status().isBadRequest())
          .andDo(print());

    }


    @Test
    @DisplayName("????????? ?????? ?????? ???????????? ????????????.")
    void shouldFailFollowWhenNoToken() throws Exception {
      mockMvc.perform(post("/follows/{userId}", otherId))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("????????? ?????? ?????? ??????????????? ????????????.")
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
  @DisplayName("????????? & ???????????? ?????? ????????? ??????")
  class GetFollow {

    @Test
    @DisplayName("????????? ???????????? ?????????????????? ????????? ????????????")
    void should_success_get_followings() throws Exception {
      mockingAuth();

      FollowingResponseDto response1 = FollowingResponseDto.builder()
          .userId(2L)
          .thumbnailURL("url1")
          .nickName("following1")
          .build();
      FollowingResponseDto response2 = FollowingResponseDto.builder()
          .userId(3L)
          .thumbnailURL("url2")
          .nickName("following2")
          .build();

      doReturn(List.of(response1, response2)).when(followService)
          .getFollowings(any(User.class), any(Pageable.class));

      callGetFollowingsApi("1")
          .andExpect(status().isOk())
          .andExpect(jsonPath("[0].userId").value(response1.getUserId()))
          .andExpect(jsonPath("[0].nickName").value(response1.getNickName()))
          .andExpect(jsonPath("[0].thumbnailURL").value(response1.getThumbnailURL()))
          .andExpect(jsonPath("[1].userId").value(response2.getUserId()))
          .andExpect(jsonPath("[1].nickName").value(response2.getNickName()))
          .andExpect(jsonPath("[1].thumbnailURL").value(response2.getThumbnailURL()))
          .andDo(document("follow-get-followings",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description("????????? ????????? ????????? access token")
              ),
              pathParameters(
                  parameterWithName("userId").description("????????? ????????? ????????? ?????? ID")
              ),
              queryParameters(
                  parameterWithName("page").optional()
                      .description("???????????? ?????? ????????? ?????? ????????? +\n(default: 0)"),
                  parameterWithName("size").optional()
                      .description("???????????? ?????? ????????? ?????? ?????? +\n(default: 10)")
              ),
              responseFields(
                  fieldWithPath("[].userId").description("????????? ID"),
                  fieldWithPath("[].nickName").description("????????? nickname"),
                  fieldWithPath("[].thumbnailURL").description("????????? thumbnailURL"),
                  fieldWithPath("[].followerCount").description("???????????? ????????? ???"),
                  fieldWithPath("[].createdAt").description("????????? ????????? ????????? ???????????? ??????")
              )));
    }

    private ResultActions callGetFollowingsApi(String userId) throws Exception {
      return mockMvc.perform(get("/follows/{userId}/followings", userId)
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "token"))
              .param("page", "0")
              .param("size", "2"))
          .andDo(print());
    }

    @Test
    @DisplayName("????????? ???????????? ?????????????????? ????????? ????????????")
    void should_success_get_followers() throws Exception {
      mockingAuth();

      FollowerResponseDto response1 = FollowerResponseDto.builder()
          .userId(2L)
          .thumbnailURL("url1")
          .nickName("follower1")
          .build();
      FollowerResponseDto response2 = FollowerResponseDto.builder()
          .userId(3L)
          .thumbnailURL("url2")
          .nickName("follower2")
          .build();

      doReturn(List.of(response1, response2)).when(followService)
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
                  cookieWithName(ACCESS_TOKEN.getName()).description("????????? ????????? ????????? access token")
              ),
              pathParameters(
                  parameterWithName("userId").description("????????? ????????? ????????? ?????? ID")
              ),
              queryParameters(
                  parameterWithName("page").optional()
                      .description("???????????? ?????? ????????? ?????? ????????? +\n(default: 0)"),
                  parameterWithName("size").optional()
                      .description("???????????? ?????? ????????? ?????? ?????? +\n(default: 10)")
              ),
              responseFields(
                  fieldWithPath("[].userId").description("????????? ID"),
                  fieldWithPath("[].nickName").description("????????? nickname"),
                  fieldWithPath("[].thumbnailURL").description("????????? thumbnailURL"),
                  fieldWithPath("[].followerCount").description("???????????? ????????? ???"),
                  fieldWithPath("[].createdAt").description("???????????? ????????? ???????????? ??????")
              )));
    }

    private ResultActions callGetFollowersApi(String userId) throws Exception {
      return mockMvc.perform(get("/follows/{userId}/followers", userId)
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "token"))
              .param("page", "0")
              .param("size", "2"))
          .andDo(print());
    }

  }
}
