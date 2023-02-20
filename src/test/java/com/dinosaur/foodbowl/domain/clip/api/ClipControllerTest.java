package com.dinosaur.foodbowl.domain.clip.api;

import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.ACCESS_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.clip.dto.response.ClipStatusResponseDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

class ClipControllerTest extends IntegrationTest {

  @Nested
  @DisplayName("클립")
  class ClipTest {

    @Test
    @DisplayName("클립 성공")
    void should_success_when_clip() throws Exception {
      mockingAuth();

      ClipStatusResponseDto response = ClipStatusResponseDto.from("ok");

      doReturn(response).when(clipService).clip(any(User.class), anyLong());

      callClipApi("1")
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value("ok"))
          .andDo(document("clip",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description("사용자 인증에 필요한 access token")
              ),
              pathParameters(
                  parameterWithName("id").description("북마크하고자 하는 게시글 ID")
              ),
              responseFields(
                  fieldWithPath("status").description("수행 상태 +\n(북마크한 것으로 변경하고 ok 반환)")
              )));
    }

    private ResultActions callClipApi(String id) throws Exception {
      return mockMvc.perform(post("/clips/posts/{id}/clip", id)
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "token")))
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("언클립")
  class UnclipTest {

    @Test
    @DisplayName("언클립 성공")
    void should_success_when_unclip() throws Exception {
      mockingAuth();

      ClipStatusResponseDto response = ClipStatusResponseDto.from("ok");

      doReturn(response).when(clipService).unclip(any(User.class), anyLong());

      callUnclipApi("1")
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value("ok"))
          .andDo(document("unclip",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description("사용자 인증에 필요한 access token")
              ),
              pathParameters(
                  parameterWithName("id").description("북마크 해제하고자 하는 게시글 ID")
              ),
              responseFields(
                  fieldWithPath("status").description("수행 상태 +\n(북마크 해제한 것으로 변경하고 ok 반환)")
              )));
    }

    private ResultActions callUnclipApi(String id) throws Exception {
      return mockMvc.perform(post("/clips/posts/{id}/unclip", id)
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "token")))
          .andDo(print());
    }
  }
}
