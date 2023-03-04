package com.dinosaur.foodbowl.domain.clip.api;

import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.ACCESS_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
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
import com.dinosaur.foodbowl.domain.clip.dto.response.ClipPostThumbnailResponse;
import com.dinosaur.foodbowl.domain.clip.dto.response.ClipStatusResponseDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import jakarta.servlet.http.Cookie;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
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

  @Nested
  @DisplayName("특정 사용자의 북마크한 게시글 썸네일 목록 조회 기능")
  class GetClipPostThumbnails {

    @Test
    @DisplayName("특정 사용자의 북마크한 게시글 썸네일 목록을 성공적으로 조회한다.")
    void success_api() throws Exception {
      mockingAuth();

      List<ClipPostThumbnailResponse> response = List.of(
          new ClipPostThumbnailResponse(1L, "path1"),
          new ClipPostThumbnailResponse(2L, "path2")
      );

      doReturn(response).when(clipService)
          .getClipPostThumbnails(any(User.class), any(Pageable.class));

      callGetClipPostThumbnailsApi()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].clipId").value(1L))
          .andExpect(jsonPath("$[0].thumbnailPath").value("path1"))
          .andExpect(jsonPath("$[1].clipId").value(2L))
          .andExpect(jsonPath("$[1].thumbnailPath").value("path2"))
          .andDo(document("clip-post-thumbnail-list",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description("사용자 인증에 필요한 access token")
              ),
              queryParameters(
                  parameterWithName("page").optional()
                      .description("북마크한 게시글 썸네일 목록 페이지 +\n(default: 0)"),
                  parameterWithName("size").optional()
                      .description("북마크한 게시글 썸네일 목록 크기 +\n(default: 18)")
              ),
              responseFields(
                  fieldWithPath("[].clipId").description("북마크 ID"),
                  fieldWithPath("[].thumbnailPath").description("북마크한 게시글 썸네일 경로")
              )));
    }

    private ResultActions callGetClipPostThumbnailsApi() throws Exception {
      return mockMvc.perform(get("/clips/thumbnails")
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "token")))
          .andDo(print());
    }
  }
}
