package com.dinosaur.foodbowl.domain.post.api;

import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.ACCESS_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.post.dto.response.PostThumbnailResponseDto;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.ResultActions;

class PostControllerTest extends IntegrationTest {

  @Nested
  @DisplayName("유저 게시글 썸네일 목록 불러오기")
  class GetThumbnails {

    @Test
    @DisplayName("썸네일 목록 불러오기 성공")
    void should_success_when_getThumbnails() throws Exception {
      mockingAuth();

      LocalDateTime now = LocalDateTime.now();

      PostThumbnailResponseDto response1 = PostThumbnailResponseDto.builder()
          .postId(1L)
          .thumbnailPath("path1")
          .createdAt(now)
          .build();
      PostThumbnailResponseDto response2 = PostThumbnailResponseDto.builder()
          .postId(2L)
          .thumbnailPath("path2")
          .createdAt(now)
          .build();

      doReturn(List.of(response1, response2)).when(postService)
          .getThumbnails(anyLong(), any(Pageable.class));

      callGetThumbnailsApi("1")
          .andExpect(status().isOk())
          .andExpect(jsonPath("[0].postId").value(response1.getPostId()))
          .andExpect(jsonPath("[0].thumbnailPath").value(response1.getThumbnailPath()))
          .andExpect(jsonPath("[0].createdAt")
              .value(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
          .andExpect(jsonPath("[1].postId").value(response2.getPostId()))
          .andExpect(jsonPath("[1].thumbnailPath").value(response2.getThumbnailPath()))
          .andExpect(jsonPath("[1].createdAt")
              .value(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
          .andDo(document("post-thumbnail-list",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description("사용자 인증에 필요한 access token")
              ),
              pathParameters(
                  parameterWithName("id").description("게시글 썸네일 목록을 불러오고 싶은 유저 ID")
              ),
              queryParameters(
                  parameterWithName("page").optional()
                      .description("불러오고 싶은 썸네일 목록 페이지 +\n(default: 0)"),
                  parameterWithName("size").optional()
                      .description("불러오고 싶은 썸네일 목록 크기 +\n(default: 18)")
              ),
              responseFields(
                  fieldWithPath("[].postId").description("게시글 ID"),
                  fieldWithPath("[].thumbnailPath").description("게시글 썸네일 URI"),
                  fieldWithPath("[].createdAt").description("게시글 생성 시간")
              )));
    }

    @Test
    @DisplayName("ID로 변환할 수 없으면 예외가 발생한다.")
    void should_throwException_when_IdNotConvert() throws Exception {
      mockingAuth();

      callGetThumbnailsApi("hello")
          .andExpect(status().isBadRequest());
    }

    private ResultActions callGetThumbnailsApi(String id) throws Exception {
      return mockMvc.perform(get("/posts/users/{id}/thumbnails", id)
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "token"))
              .param("page", "0")
              .param("size", "2"))
          .andDo(print());
    }
  }
}
