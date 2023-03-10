package com.dinosaur.foodbowl.domain.post.api;

import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.ACCESS_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.address.dto.requset.AddressRequestDto;
import com.dinosaur.foodbowl.domain.post.dto.request.PostCreateRequestDto;
import com.dinosaur.foodbowl.domain.post.dto.request.PostUpdateRequestDto;
import com.dinosaur.foodbowl.domain.post.dto.response.PostFeedResponseDto;
import com.dinosaur.foodbowl.domain.post.dto.response.PostThumbnailResponse;
import com.dinosaur.foodbowl.domain.store.dto.request.StoreRequestDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;

public class PostControllerTest extends IntegrationTest {

  @Autowired
  ObjectMapper objectMapper;

  @Nested
  @DisplayName("????????? ??????")
  class CreatePost {

    @Nested
    @DisplayName("?????? ?????????")
    public class Success {

      @Test
      @DisplayName("????????? ???????????? ????????? ????????? ????????????.")
      void should_success_create_post() throws Exception {
        mockingAuth();

        StoreRequestDto storeRequestDto = postTestHelper.generateStoreDto();
        AddressRequestDto addressRequestDto = postTestHelper.generateAddressDto();
        PostCreateRequestDto requestDto = postTestHelper.getPostCreateRequestDto(storeRequestDto,
            addressRequestDto);
        String requestToJason = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
            requestToJason.getBytes(StandardCharsets.UTF_8));
        long createdPostId = 1L;
        doReturn(createdPostId).when(postService).createPost(any(User.class), any(), any());

        callCreatePostApi(request)
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, "/posts/" + createdPostId))
            .andDo(document("post-create",
                requestCookies(
                    cookieWithName(ACCESS_TOKEN.getName()).description("????????? ????????? ????????? access token")
                ),
                requestParts(
                    partWithName("images").description("????????? ?????? ?????????"),
                    partWithName("request").description("????????? DATA")
                ),
                requestPartFields("request",
                    fieldWithPath("content").description("????????? ??????"),
                    fieldWithPath("store.storeName").description("?????? ??????"),
                    fieldWithPath("address.addressName").description("?????? ????????? ??????"),
                    fieldWithPath("address.region1depthName").description("?????????1"),
                    fieldWithPath("address.region2depthName").description("?????????2"),
                    fieldWithPath("address.region3depthName").description("?????????3"),
                    fieldWithPath("address.roadName").description("?????????"),
                    fieldWithPath("address.mainBuildingNo").description("?????? ??????"),
                    fieldWithPath("address.subBuildingNo").description("?????? ??????").optional(),
                    fieldWithPath("address.longitude").description("??????").optional(),
                    fieldWithPath("address.latitude").description("??????").optional(),
                    fieldWithPath("categoryIds").description("???????????? ID ?????????")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.LOCATION).description("????????? ???????????? URI ?????????.")
                )
            ));
      }
    }

    @Nested
    @DisplayName("?????? ?????????")
    public class Fail {

      @Test
      @DisplayName("????????? ????????? ????????? ????????? ????????????.")
      void should_fail_when_without_image() throws Exception {
        mockingAuth();
        StoreRequestDto storeRequestDto = postTestHelper.generateStoreDto();
        AddressRequestDto addressRequestDto = postTestHelper.generateAddressDto();
        PostCreateRequestDto requestDto = postTestHelper.getPostCreateRequestDto(storeRequestDto,
            addressRequestDto);

        String requestToJason = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
            requestToJason.getBytes(StandardCharsets.UTF_8));

        doReturn(1L).when(postService).createPost(any(User.class), any(), any());

        callCreatePostApiWithoutImage(request)
            .andExpect(status().isBadRequest());
      }

      @Test
      @DisplayName("????????? ????????? ????????? ????????? ????????????.")
      void should_fail_when_without_store() throws Exception {
        mockingAuth();
        AddressRequestDto addressRequestDto = postTestHelper.generateAddressDto();
        PostCreateRequestDto requestDto = postTestHelper.getPostCreateRequestDto(null,
            addressRequestDto);
        String requestToJason = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
            requestToJason.getBytes(StandardCharsets.UTF_8));

        doReturn(1L).when(postService).createPost(any(User.class), any(), any());

        callCreatePostApi(request)
            .andExpect(status().isBadRequest());
      }

      @Test
      @DisplayName("????????? ????????? ????????? ????????? ????????? ????????????.")
      void should_fail_when_without_address() throws Exception {
        mockingAuth();
        StoreRequestDto storeRequestDto = postTestHelper.generateStoreDto();
        PostCreateRequestDto requestDto = postTestHelper.getPostCreateRequestDto(storeRequestDto,
            null);
        String requestToJason = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
            requestToJason.getBytes(StandardCharsets.UTF_8));

        doReturn(1L).when(postService).createPost(any(User.class), any(), any());

        callCreatePostApi(request)
            .andExpect(status().isBadRequest());
      }
    }

    private ResultActions callCreatePostApi(MockMultipartFile request)
        throws Exception {
      return mockMvc.perform(multipart("/posts")
              .file(photoTestHelper.getImageFile())
              .file(request)
              .contentType(MediaType.MULTIPART_FORM_DATA)
              .accept(MediaType.APPLICATION_JSON)
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "token")))
          .andDo(print());
    }

    private ResultActions callCreatePostApiWithoutImage(MockMultipartFile request)
        throws Exception {
      return mockMvc.perform(multipart("/posts")
              .file(request)
              .contentType(MediaType.MULTIPART_FORM_DATA)
              .accept(MediaType.APPLICATION_JSON)
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "token")))
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("????????? ??????")
  class UpdatePost {

    @Nested
    @DisplayName("?????? ?????????")
    public class Success {

      @Test
      @DisplayName("????????? ?????? ??????")
      void should_success_when_update_post() throws Exception {
        mockingAuth();
        Long postId = 1L;
        PostUpdateRequestDto requestDto = postTestHelper.getValidPostUpdateRequestDto();
        String requestToJason = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
            requestToJason.getBytes(StandardCharsets.UTF_8));

        doReturn(postId).when(postService).updatePost(any(User.class), any(), any(), any());

        callUpdatePostApi(postId, request)
            .andExpect(status().isOk())
            .andDo(document("post-update",
                requestCookies(
                    cookieWithName(ACCESS_TOKEN.getName()).description("????????? ????????? ????????? access token")
                ),
                pathParameters(
                    parameterWithName("id").description("??????????????? ?????? ????????? ID")
                ),
                requestParts(
                    partWithName("images").description("????????? ?????? ?????????"),
                    partWithName("request").description("????????? ?????? Data")
                ),
                requestPartFields(
                    "request",
                    fieldWithPath("content").description("??????????????? ????????? ??????"),
                    fieldWithPath("store").description("??????????????? ?????? ??????"),
                    fieldWithPath("store.storeName").description("?????? ??????"),
                    fieldWithPath("address").description("??????????????? ?????? ??????"),
                    fieldWithPath("address.addressName").description("?????? ????????? ??????"),
                    fieldWithPath("address.region1depthName").description("?????????1"),
                    fieldWithPath("address.region2depthName").description("?????????2"),
                    fieldWithPath("address.region3depthName").description("?????????3"),
                    fieldWithPath("address.roadName").description("?????????"),
                    fieldWithPath("address.mainBuildingNo").description("?????? ??????"),
                    fieldWithPath("address.subBuildingNo").description("?????? ??????").optional(),
                    fieldWithPath("address.longitude").description("??????").optional(),
                    fieldWithPath("address.latitude").description("??????").optional(),
                    fieldWithPath("categoryIds").description("??????????????? ???????????? ID ?????????")
                )));
      }
    }

    @Nested
    @DisplayName("?????? ?????????")
    public class Fail {

      @Test
      @DisplayName("????????? ????????? ????????? ????????? ????????????.")
      void should_fail_when_without_image() throws Exception {
        mockingAuth();
        Long postId = 1L;
        PostUpdateRequestDto requestDto = postTestHelper.getValidPostUpdateRequestDto();
        String requestToJason = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
            requestToJason.getBytes(StandardCharsets.UTF_8));

        doReturn(postId).when(postService).updatePost(any(User.class), any(), any(), any());

        callUpdatePostApiWithOutImages(postId, request)
            .andExpect(status().isBadRequest());
      }

      @Test
      @DisplayName("????????? ????????? ????????? ????????? ????????????.")
      void should_fail_when_without_store() throws Exception {
        mockingAuth();
        Long postId = 1L;
        AddressRequestDto addressRequestDto = postTestHelper.generateAddressDto();
        PostUpdateRequestDto requestDto = postTestHelper.getPostUpdateRequestDto(
            null, addressRequestDto, List.of(1L));
        String requestToJason = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
            requestToJason.getBytes(StandardCharsets.UTF_8));

        doReturn(postId).when(postService).updatePost(any(User.class), any(), any(), any());

        callUpdatePostApi(postId, request)
            .andExpect(status().isBadRequest());
      }

      @Test
      @DisplayName("????????? ????????? ????????? ????????? ????????? ????????????.")
      void should_fail_when_without_address() throws Exception {
        mockingAuth();
        Long postId = 1L;
        StoreRequestDto storeRequestDto = postTestHelper.generateStoreDto();
        PostUpdateRequestDto requestDto = postTestHelper.getPostUpdateRequestDto(
            storeRequestDto, null, List.of(1L));
        String requestToJason = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
            requestToJason.getBytes(StandardCharsets.UTF_8));

        doReturn(postId).when(postService).updatePost(any(User.class), any(), any(), any());

        callUpdatePostApi(postId, request)
            .andExpect(status().isBadRequest());
      }
    }

    private ResultActions callUpdatePostApiWithOutImages(Long id, MockMultipartFile request)
        throws Exception {
      return mockMvc.perform(
              RestDocumentationRequestBuilders.multipart("/posts/{id}", id)
                  .file(request)
                  .contentType(MediaType.MULTIPART_FORM_DATA)
                  .accept(MediaType.APPLICATION_JSON)
                  .cookie(new Cookie(ACCESS_TOKEN.getName(), "token")))
          .andDo(print());
    }

    private ResultActions callUpdatePostApi(Long id, MockMultipartFile request)
        throws Exception {
      return mockMvc.perform(
              RestDocumentationRequestBuilders.multipart("/posts/{id}", id)
                  .file(photoTestHelper.getImageFile())
                  .file(request)
                  .contentType(MediaType.MULTIPART_FORM_DATA)
                  .accept(MediaType.APPLICATION_JSON)
                  .cookie(new Cookie(ACCESS_TOKEN.getName(), "token")))
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("????????? ??????")
  class DeletePost {

    @Test
    @DisplayName("????????? ????????? ????????????.")
    void should_success_when_delete_post() throws Exception {
      mockingAuth();
      Long postId = 1L;

      doNothing().when(postService).deletePost((any(User.class)), anyLong());

      callDeletePostApi(postId)
          .andExpect(status().isNoContent())
          .andDo(document("post-delete",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description("????????? ????????? ????????? access token")
              ),
              pathParameters(
                  parameterWithName("id").description("??????????????? ?????? ????????? ID")
              )));
    }

    private ResultActions callDeletePostApi(Long postId) throws Exception {
      return mockMvc.perform(delete("/posts/{id}", postId)
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "token")))
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("?????? ????????? ????????? ?????? ????????????")
  class GetThumbnails {

    @Test
    @DisplayName("????????? ?????? ???????????? ??????")
    void should_success_when_getThumbnails() throws Exception {
      mockingAuth();

      LocalDateTime now = LocalDateTime.now();

      List<PostThumbnailResponse> response = List.of(
          new PostThumbnailResponse(1L, "path1", now),
          new PostThumbnailResponse(2L, "path2", now)
      );

      doReturn(response).when(postService).getWrittenPostThumbnails(anyLong(), any(Pageable.class));

      callGetThumbnailsApi("1")
          .andExpect(status().isOk())
          .andExpect(jsonPath("[0].postId").value(1L))
          .andExpect(jsonPath("[0].thumbnailPath").value("path1"))
          .andExpect(jsonPath("[0].createdAt")
              .value(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
          .andExpect(jsonPath("[1].postId").value(2L))
          .andExpect(jsonPath("[1].thumbnailPath").value("path2"))
          .andExpect(jsonPath("[1].createdAt")
              .value(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
          .andDo(document("post-thumbnail-list-by-written",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description("????????? ????????? ????????? access token")
              ),
              pathParameters(
                  parameterWithName("id").description("????????? ????????? ????????? ???????????? ?????? ?????? ID")
              ),
              queryParameters(
                  parameterWithName("page").optional()
                      .description("???????????? ?????? ????????? ?????? ????????? +\n(default: 0)"),
                  parameterWithName("size").optional()
                      .description("???????????? ?????? ????????? ?????? ?????? +\n(default: 18)")
              ),
              responseFields(
                  fieldWithPath("[].postId").description("????????? ID"),
                  fieldWithPath("[].thumbnailPath").description("????????? ????????? URI"),
                  fieldWithPath("[].createdAt").description("????????? ?????? ??????")
              )));
    }

    @Test
    @DisplayName("ID??? ????????? ??? ????????? ????????? ????????????.")
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

  @Nested
  @DisplayName("?????? ????????? ?????? ?????? ????????????")
  class GetFeed {

    @Test
    @DisplayName("????????? ?????? ?????? ???????????? ??????")
    void should_success_when_getFeed() throws Exception {
      mockingAuth();

      PostFeedResponseDto mockResponse = PostFeedResponseDto.builder()
          .nickname("?????????")
          .thumbnailPath("ThumbnailPath")
          .followerCount(100)
          .photoPaths(List.of("PhotoPath1", "PhotoPath2"))
          .storeName("???????????? ?????????")
          .categories(List.of("??????"))
          .latitude(new BigDecimal(17.561))
          .longitude(new BigDecimal(18.9078))
          .content("?????? ?????? ???????????? ?????? ????????????!")
          .clipCount(4)
          .clipStatus(false)
          .commentCount(51)
          .createdAt(LocalDateTime.now())
          .build();

      doReturn(List.of(mockResponse)).when(postService)
          .getFeed(any(User.class), any(Pageable.class));

      callGetFeedApi()
          .andExpect(status().isOk())
          .andExpect(jsonPath("[0].nickname").value("?????????"))
          .andExpect(jsonPath("[0].thumbnailPath").value("ThumbnailPath"))
          .andExpect(jsonPath("[0].followerCount").value(100))
          .andExpect(jsonPath("[0].storeName").value("???????????? ?????????"))
          .andExpect(jsonPath("[0].latitude").value(new BigDecimal(17.561)))
          .andExpect(jsonPath("[0].longitude").value(new BigDecimal(18.9078)))
          .andExpect(jsonPath("[0].content").value("?????? ?????? ???????????? ?????? ????????????!"))
          .andExpect(jsonPath("[0].clipCount").value(4))
          .andExpect(jsonPath("[0].clipStatus").value(false))
          .andExpect(jsonPath("[0].commentCount").value(51))
          .andDo(document("post-feed",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description("????????? ????????? ????????? access token")
              ),
              queryParameters(
                  parameterWithName("page").optional()
                      .description("???????????? ?????? ?????? ?????? ????????? +\n(default: 0)"),
                  parameterWithName("size").optional()
                      .description("???????????? ?????? ?????? ?????? ?????? +\n(default: 4)")
              ),
              responseFields(
                  fieldWithPath("[].nickname").description("????????? ????????? ?????????"),
                  fieldWithPath("[].thumbnailPath").description("????????? ????????? ????????? URI"),
                  fieldWithPath("[].followerCount").description("????????? ????????? ????????? ???"),
                  fieldWithPath("[].photoPaths").description("????????? ?????? URI ??????"),
                  fieldWithPath("[].storeName").description("?????? ??????"),
                  fieldWithPath("[].categories").description("???????????? ?????? ??????"),
                  fieldWithPath("[].latitude").description("?????? ??????"),
                  fieldWithPath("[].longitude").description("?????? ??????"),
                  fieldWithPath("[].content").description("????????? ??????"),
                  fieldWithPath("[].clipCount").description("????????? ????????? ???"),
                  fieldWithPath("[].clipStatus").description("????????? ????????? ??????"),
                  fieldWithPath("[].commentCount").description("????????? ?????? ???"),
                  fieldWithPath("[].createdAt").description("????????? ?????? ??????")
              )));
    }

    private ResultActions callGetFeedApi() throws Exception {
      return mockMvc.perform(get("/posts/feed")
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "token"))
              .param("page", "0")
              .param("size", "4"))
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("?????? ???????????? ????????? ?????? ????????? ????????? ?????? ??????")
  class GetPostThumbnails {

    @Test
    @DisplayName("?????? ???????????? ????????? ?????? ????????? ????????? ????????? ????????????.")
    void successApi() throws Exception {
      mockingAuth();

      LocalDateTime now = LocalDateTime.now();

      List<PostThumbnailResponse> response = List.of(
          new PostThumbnailResponse(1L, "path1", now),
          new PostThumbnailResponse(2L, "path2", now)
      );

      doReturn(response).when(postService).getPostThumbnails(any(User.class), any(Pageable.class));

      callGetPostThumbnailsApi()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].postId").value(1L))
          .andExpect(jsonPath("$[0].thumbnailPath").value("path1"))
          .andExpect(jsonPath("$[0].createdAt")
              .value(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
          .andExpect(jsonPath("$[1].postId").value(2L))
          .andExpect(jsonPath("$[1].thumbnailPath").value("path2"))
          .andExpect(jsonPath("$[1].createdAt")
              .value(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
          .andDo(document("post-thumbnail-list",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description("????????? ????????? ????????? access token")
              ),
              queryParameters(
                  parameterWithName("page").optional()
                      .description("????????? ????????? ?????? ????????? +\n(default: 0)"),
                  parameterWithName("size").optional()
                      .description("????????? ????????? ?????? ?????? +\n(default: 18)")
              ),
              responseFields(
                  fieldWithPath("[].postId").description("????????? ID"),
                  fieldWithPath("[].thumbnailPath").description("????????? ????????? ??????"),
                  fieldWithPath("[].createdAt").description("????????? ?????? ??????")
              )));
    }

    private ResultActions callGetPostThumbnailsApi() throws Exception {
      return mockMvc.perform(get("/posts/thumbnails")
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "token")))
          .andDo(print());
    }
  }
}
