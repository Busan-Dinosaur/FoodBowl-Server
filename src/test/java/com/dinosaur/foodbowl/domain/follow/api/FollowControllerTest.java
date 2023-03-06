package com.dinosaur.foodbowl.domain.follow.api;

import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.ACCESS_TOKEN;
import static org.mockito.ArgumentMatchers.anyLong;
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
import com.dinosaur.foodbowl.domain.user.entity.Role.RoleType;
import com.dinosaur.foodbowl.domain.user.entity.User;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;

public class FollowControllerTest extends IntegrationTest {

    @Nested
    class 팔로우_언팔로우 {

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
        void 팔로우에_성공하면_204_반환한다() throws Exception {
            callFollowApi(otherId).andExpect(status().isNoContent())
                    .andDo(print())
                    .andDo(document("follow",
                            requestCookies(
                                    cookieWithName(ACCESS_TOKEN.getName()).description(
                                            "로그인이나 회원가입 시 얻을 수 있는 접근 토큰입니다. \n\n"
                                                    + "만료 시간: "
                                                    + ACCESS_TOKEN.getValidMilliSecond() / 1000
                                                    + "초"
                                    )
                            ),
                            pathParameters(
                                    parameterWithName("userId").description("팔로우할 유저의 아이디")
                            )));
        }

        @Test
        void 팔로우_취소에_성공하면_204_반환한다() throws Exception {
            callUnfollowApi(otherId).andExpect(status().isNoContent())
                    .andDo(print())
                    .andDo(document("unfollow",
                            requestCookies(
                                    cookieWithName(ACCESS_TOKEN.getName()).description(
                                            "로그인이나 회원가입 시 얻을 수 있는 접근 토큰입니다. \n\n"
                                                    + "만료 시간: "
                                                    + ACCESS_TOKEN.getValidMilliSecond() / 1000
                                                    + "초"
                                    )
                            ),
                            pathParameters(
                                    parameterWithName("userId").description("팔로우를 취소할 유저의 아이디")
                            )));
        }

        @Test
        void 본인_팔로우는_400_반환한다() throws Exception {
            callFollowApi(myId).andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        void 본인_팔로우_취소는_400_반환한다() throws Exception {
            callUnfollowApi(myId).andExpect(status().isBadRequest())
                    .andDo(print());

        }


        @Test
        void 토큰이_없으면_팔로우는_실패한다() throws Exception {
            mockMvc.perform(post("/api/v1/follows/{userId}", otherId))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void 토큰이_없으면_팔로우_취소는_실패한다() throws Exception {
            mockMvc.perform(delete("/api/v1/follows/{userId}", otherId))
                    .andExpect(status().isUnauthorized());
        }


        private ResultActions callFollowApi(Long userId) throws Exception {
            return mockMvc.perform(post("/api/v1/follows/{userId}", userId)
                    .cookie(new Cookie(ACCESS_TOKEN.getName(), userToken))
                    .contentType(MediaType.APPLICATION_JSON));
        }

        private ResultActions callUnfollowApi(Long userId) throws Exception {
            return mockMvc.perform(delete("/api/v1/follows/{userId}", userId)
                    .cookie(new Cookie(ACCESS_TOKEN.getName(), userToken))
                    .contentType(MediaType.APPLICATION_JSON));
        }
    }
}
