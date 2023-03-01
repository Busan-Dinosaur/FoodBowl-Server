package com.dinosaur.foodbowl.domain.comment.api;

import static com.dinosaur.foodbowl.domain.comment.entity.Comment.MAX_MESSAGE_LENGTH;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.ACCESS_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.comment.dto.request.CommentWriteRequestDto;
import com.dinosaur.foodbowl.domain.comment.dto.response.CommentResponseDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class CommentControllerTest extends IntegrationTest {

  @Nested
  class 댓글_작성 {

    @Test
    void 댓글_작성에_성공한다() throws Exception {
      mockingAuth();
      doNothing().when(commentService)
          .writeComment(any(User.class), any(CommentWriteRequestDto.class));

      CommentWriteRequestDto request = CommentWriteRequestDto.builder()
          .postId(1L)
          .message("테스트 댓글")
          .build();

      callWriteCommentApi(request)
          .andExpect(status().isSeeOther())
          .andExpect(header().string("Location", "/api/v1/comments/posts/" + 1))
          .andDo(document("comment-write",
              requestFields(
                  fieldWithPath("postId").description("댓글을 작성하고자 하는 게시글 ID"),
                  fieldWithPath("message").description("사용자가 작성한 댓글")
              ),
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description("사용자 인증에 필요한 access token")
              ),
              responseHeaders(
                  headerWithName("Location").description("해당 게시글 댓글 목록을 불러오기 위한 Redirect URI")
              )));
    }

    @Test
    void 엑세스_토큰이_존재하지_않으면_예외가_발생한다() throws Exception {
      CommentWriteRequestDto request = CommentWriteRequestDto.builder()
          .postId(1L)
          .message("테스트 댓글")
          .build();

      mockMvc.perform(post("/api/v1/comments")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(request)))
          .andDo(print())
          .andExpect(status().isUnauthorized());
    }

    @Test
    void 최대_메시지_글자_수를_초과하면_예외가_발생한다() throws Exception {
      mockingAuth();

      CommentWriteRequestDto request = CommentWriteRequestDto.builder()
          .postId(1L)
          .message("가".repeat(MAX_MESSAGE_LENGTH + 1))
          .build();

      mockMvc.perform(post("/api/v1/comments")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(request)))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }

    private ResultActions callWriteCommentApi(CommentWriteRequestDto request) throws Exception {
      return mockMvc.perform(post("/api/v1/comments")
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "token"))
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(request)))
          .andDo(print());
    }
  }

  @Nested
  class 댓글_수정 {

    @Test
    void 댓글_수정에_성공한다() throws Exception {
      mockingAuth();

      long postId = 1l;
      doReturn(postId).when(commentService).updateComment(any(User.class), anyLong(), anyString());

      callUpdateCommentApi("1", "update Message")
          .andExpect(status().isSeeOther())
          .andExpect(header().string("Location", "/api/v1/comments/posts/" + postId))
          .andDo(document("comment-update",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description("사용자 인증에 필요한 access token")
              ),
              pathParameters(
                  parameterWithName("id").description("수정하고자 하는 댓글 ID")
              ),
              queryParameters(
                  parameterWithName("message").description("업데이트하고자 하는 댓글 내용")
              ),
              responseHeaders(
                  headerWithName("Location").description("해당 게시글 댓글 목록을 불러오기 위한 Redirect URI")
              )));
    }

    @Test
    void ID로_변환할_수_없으면_예외가_발생한다() throws Exception {
      mockingAuth();

      callUpdateCommentApi("hello", "update Message")
          .andExpect(status().isBadRequest());
    }

    @Test
    void 댓글이_빈칸이면_예외가_발생한다() throws Exception {
      mockingAuth();

      callUpdateCommentApi("1", "  ")
          .andExpect(status().isBadRequest());
    }

    @Test
    void 댓글이_최대_글자_수를_초과하면_예외가_발생한다() throws Exception {
      mockingAuth();

      callUpdateCommentApi("1", "a".repeat(MAX_MESSAGE_LENGTH + 1))
          .andExpect(status().isBadRequest());
    }

    private ResultActions callUpdateCommentApi(String id, String message) throws Exception {
      return mockMvc.perform(patch("/api/v1/comments/{id}", id)
              .queryParam("message", message)
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "token")))
          .andDo(print());
    }
  }

  @Nested
  class 댓글_삭제 {

    @Test
    void 댓글_삭제에_성공한다() throws Exception {
      mockingAuth();

      doNothing().when(commentService).deleteComment(any(User.class), anyLong());

      callDeleteCommentApi("1")
          .andExpect(status().isNoContent())
          .andDo(document("comment-delete",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description("사용자 인증에 필요한 access token")
              ),
              pathParameters(
                  parameterWithName("id").description("삭제하고자 하는 댓글 ID")
              )));
    }

    @Test
    void ID로_변환할_수_없으면_예외가_발생한다() throws Exception {
      mockingAuth();

      callDeleteCommentApi("hello")
          .andExpect(status().isBadRequest());
    }

    private ResultActions callDeleteCommentApi(String id) throws Exception {
      return mockMvc.perform(delete("/api/v1/comments/{id}", id)
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "token")))
          .andDo(print());
    }
  }

  @Nested
  class 제한되지_않은_댓글_시간순_조회 {

    @Test
    void 댓글_목록_조회에_성공한다() throws Exception {
      mockingAuth();

      LocalDateTime now = LocalDateTime.now();

      CommentResponseDto response1 = CommentResponseDto.builder()
          .nickname("nickname1")
          .userThumbnailPath("path1")
          .message("message1")
          .createdAt(now)
          .build();
      CommentResponseDto response2 = CommentResponseDto.builder()
          .nickname("nickname2")
          .userThumbnailPath("path2")
          .message("message2")
          .createdAt(now)
          .build();

      doReturn(List.of(response1, response2)).when(commentService).getComments(anyLong());

      callGetCommentsApi("1")
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].nickname").value(response1.getNickname()))
          .andExpect(jsonPath("$[0].userThumbnailPath").value(response1.getUserThumbnailPath()))
          .andExpect(jsonPath("$[0].message").value(response1.getMessage()))
          .andExpect(jsonPath("$[0].createdAt")
              .value(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
          .andExpect(jsonPath("$[1].nickname").value(response2.getNickname()))
          .andExpect(jsonPath("$[1].userThumbnailPath").value(response2.getUserThumbnailPath()))
          .andExpect(jsonPath("$[1].message").value(response2.getMessage()))
          .andExpect(jsonPath("$[1].createdAt")
              .value(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
          .andDo(document("comment-list",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description("사용자 인증에 필요한 access token")
              ),
              pathParameters(
                  parameterWithName("id").description("댓글 조회하고자 하는 게시글 ID")
              ),
              responseFields(
                  fieldWithPath("[].nickname").description("유저 닉네임"),
                  fieldWithPath("[].userThumbnailPath").description("유저 썸네일 +\n존재하지 않으면 null"),
                  fieldWithPath("[].message").description("유저가 작성한 댓글 내용"),
                  fieldWithPath("[].createdAt").description("댓글 작성 시간 +\n(yyyy-MM-dd'T'HH:mm:ss")
              )));
    }

    @Test
    void ID로_변환할_수_없으면_예외가_발생한다() throws Exception {
      mockingAuth();

      callGetCommentsApi("hello")
          .andExpect(status().isBadRequest());
    }

    private ResultActions callGetCommentsApi(String id) throws Exception {
      return mockMvc.perform(get("/api/v1/comments/posts/{id}", id)
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "token")))
          .andDo(print());
    }
  }
}
