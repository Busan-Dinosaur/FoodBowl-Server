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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class CommentControllerTest extends IntegrationTest {

  @Nested
  @DisplayName("?????? ??????")
  class WriteComment {

    @Test
    @DisplayName("?????? ?????? ??????")
    void should_success_when_writeComment() throws Exception {
      mockingAuth();
      doNothing().when(commentService)
          .writeComment(any(User.class), any(CommentWriteRequestDto.class));

      CommentWriteRequestDto request = CommentWriteRequestDto.builder()
          .postId(1L)
          .message("????????? ??????")
          .build();

      callWriteCommentApi(request)
          .andExpect(status().isSeeOther())
          .andExpect(header().string("Location", "/comments/posts/" + 1))
          .andDo(document("comment-write",
              requestFields(
                  fieldWithPath("postId").description("????????? ??????????????? ?????? ????????? ID"),
                  fieldWithPath("message").description("???????????? ????????? ??????")
              ),
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description("????????? ????????? ????????? access token")
              ),
              responseHeaders(
                  headerWithName("Location").description("?????? ????????? ?????? ????????? ???????????? ?????? Redirect URI")
              )));
    }

    @Test
    @DisplayName("?????? ????????? ???????????? ????????? ????????? ????????????.")
    void should_throwException_when_tokenNotExist() throws Exception {
      CommentWriteRequestDto request = CommentWriteRequestDto.builder()
          .postId(1L)
          .message("????????? ??????")
          .build();

      mockMvc.perform(post("/comments")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(request)))
          .andDo(print())
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("?????? ????????? ?????? ?????? ???????????? ????????? ????????????.")
    void should_throwException_when_messageOverLength() throws Exception {
      mockingAuth();

      CommentWriteRequestDto request = CommentWriteRequestDto.builder()
          .postId(1L)
          .message("???".repeat(MAX_MESSAGE_LENGTH + 1))
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
  @DisplayName("?????? ??????")
  class UpdateComment {

    @Test
    @DisplayName("?????? ????????? ????????????.")
    void should_success_when_updateComment() throws Exception {
      mockingAuth();

      long postId = 1l;
      doReturn(postId).when(commentService).updateComment(any(User.class), anyLong(), anyString());

      callUpdateCommentApi("1", "update Message")
          .andExpect(status().isSeeOther())
          .andExpect(header().string("Location", "/comments/posts/" + postId))
          .andDo(document("comment-update",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description("????????? ????????? ????????? access token")
              ),
              pathParameters(
                  parameterWithName("id").description("??????????????? ?????? ?????? ID")
              ),
              queryParameters(
                  parameterWithName("message").description("????????????????????? ?????? ?????? ??????")
              ),
              responseHeaders(
                  headerWithName("Location").description("?????? ????????? ?????? ????????? ???????????? ?????? Redirect URI")
              )));
    }

    @Test
    @DisplayName("ID??? ????????? ??? ????????? ????????? ????????????.")
    void should_throwException_when_IdNotConvert() throws Exception {
      mockingAuth();

      callUpdateCommentApi("hello", "update Message")
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("????????? ???????????? ????????? ????????????.")
    void should_throwException_when_messageBlank() throws Exception {
      mockingAuth();

      callUpdateCommentApi("1", "  ")
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????? ???????????? ????????? ????????????.")
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

  @Nested
  @DisplayName("?????? ??????")
  class DeleteComment {

    @Test
    @DisplayName("?????? ????????? ????????????.")
    void should_success_when_deleteComment() throws Exception {
      mockingAuth();

      doNothing().when(commentService).deleteComment(any(User.class), anyLong());

      callDeleteCommentApi("1")
          .andExpect(status().isNoContent())
          .andDo(document("comment-delete",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description("????????? ????????? ????????? access token")
              ),
              pathParameters(
                  parameterWithName("id").description("??????????????? ?????? ?????? ID")
              )));
    }

    @Test
    @DisplayName("ID??? ????????? ??? ????????? ????????? ????????????.")
    void should_throwException_when_IdNotConvert() throws Exception {
      mockingAuth();

      callDeleteCommentApi("hello")
          .andExpect(status().isBadRequest());
    }

    private ResultActions callDeleteCommentApi(String id) throws Exception {
      return mockMvc.perform(delete("/comments/{id}", id)
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "token")))
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("???????????? ?????? ?????? ??????????????? ??????")
  class GetComments {

    @Test
    @DisplayName("?????? ?????? ???????????? ??????")
    void should_success_when_getComments() throws Exception {
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
                  cookieWithName(ACCESS_TOKEN.getName()).description("????????? ????????? ????????? access token")
              ),
              pathParameters(
                  parameterWithName("id").description("?????? ??????????????? ?????? ????????? ID")
              ),
              responseFields(
                  fieldWithPath("[].nickname").description("?????? ?????????"),
                  fieldWithPath("[].userThumbnailPath").description("?????? ????????? +\n???????????? ????????? null"),
                  fieldWithPath("[].message").description("????????? ????????? ?????? ??????"),
                  fieldWithPath("[].createdAt").description("?????? ?????? ?????? +\n(yyyy-MM-dd'T'HH:mm:ss")
              )));
    }

    @Test
    @DisplayName("ID??? ????????? ??? ????????? ????????? ????????????.")
    void should_throwException_when_IdNotConvert() throws Exception {
      mockingAuth();

      callGetCommentsApi("hello")
          .andExpect(status().isBadRequest());
    }

    private ResultActions callGetCommentsApi(String id) throws Exception {
      return mockMvc.perform(get("/comments/posts/{id}", id)
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "token")))
          .andDo(print());
    }
  }
}
