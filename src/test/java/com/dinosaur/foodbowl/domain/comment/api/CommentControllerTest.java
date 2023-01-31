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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.comment.dto.request.CommentWriteRequestDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class CommentControllerTest extends IntegrationTest {

  @Nested
  @DisplayName("댓글 작성")
  class WriteComment {

    @Test
    @DisplayName("댓글 작성 성공")
    void should_success_when_writeComment() throws Exception {
      mockingAuth();
      doNothing().when(commentService)
          .writeComment(any(User.class), any(CommentWriteRequestDto.class));

      CommentWriteRequestDto request = CommentWriteRequestDto.builder()
          .postId(1L)
          .message("테스트 댓글")
          .build();

      callWriteCommentApi(request)
          .andExpect(status().isSeeOther())
          .andExpect(header().string("Location", "/comments/posts/" + 1))
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
    @DisplayName("인증 토큰이 존재하지 않으면 예외가 발생한다.")
    void should_throwException_when_tokenNotExist() throws Exception {
      CommentWriteRequestDto request = CommentWriteRequestDto.builder()
          .postId(1L)
          .message("테스트 댓글")
          .build();

      mockMvc.perform(post("/comments")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(request)))
          .andDo(print())
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("최대 메시지 글자 수를 초과하면 예외가 발생한다.")
    void should_throwException_when_messageOverLength() throws Exception {
      mockingAuth();

      CommentWriteRequestDto request = CommentWriteRequestDto.builder()
          .postId(1L)
          .message("가".repeat(MAX_MESSAGE_LENGTH + 1))
          .build();

      mockMvc.perform(post("/comments")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(request)))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }

    private ResultActions callWriteCommentApi(CommentWriteRequestDto request) throws Exception {
      return mockMvc.perform(post("/comments")
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "token"))
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(request)))
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("댓글 수정")
  class UpdateComment {

    @Test
    @DisplayName("댓글 수정에 성공한다.")
    void should_success_when_updateComment() throws Exception {
      mockingAuth();

      long postId = 1l;
      doReturn(postId).when(commentService).updateComment(any(User.class), anyLong(), anyString());

      callUpdateCommentApi("1", "update Message")
          .andExpect(status().isSeeOther())
          .andExpect(header().string("Location", "/comments/posts/" + postId))
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
    @DisplayName("ID로 변환할 수 없으면 예외가 발생한다.")
    void should_throwException_when_IdNotConvert() throws Exception {
      mockingAuth();

      callUpdateCommentApi("hello", "update Message")
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글이 빈칸이면 예외가 발생한다.")
    void should_throwException_when_messageBlank() throws Exception {
      mockingAuth();

      callUpdateCommentApi("1", "  ")
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글이 최대 글자 수를 초과하면 예외가 발생한다.")
    void should_throwException_when_messageOverLength() throws Exception {
      mockingAuth();

      callUpdateCommentApi("1", "a".repeat(MAX_MESSAGE_LENGTH + 1))
          .andExpect(status().isBadRequest());
    }

    private ResultActions callUpdateCommentApi(String id, String message) throws Exception {
      return mockMvc.perform(patch("/comments/{id}", id)
              .queryParam("message", message)
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "token")))
          .andDo(print());
    }
  }
}
