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
  @DisplayName("게시글 생성")
  class CreatePost {

    @Nested
    @DisplayName("성공 테스트")
    public class Success {

      @Test
      @DisplayName("올바른 요청이면 게시글 생성은 성공한다.")
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
                    cookieWithName(ACCESS_TOKEN.getName()).description("사용자 인증에 필요한 access token")
                ),
                requestParts(
                    partWithName("images").description("게시글 사진 리스트"),
                    partWithName("request").description("게시글 DATA")
                ),
                requestPartFields("request",
                    fieldWithPath("content").description("게시글 내용"),
                    fieldWithPath("store.storeName").description("상점 이름"),
                    fieldWithPath("address.addressName").description("전체 도로명 주소"),
                    fieldWithPath("address.region1depthName").description("지역명1"),
                    fieldWithPath("address.region2depthName").description("지역명2"),
                    fieldWithPath("address.region3depthName").description("지역명3"),
                    fieldWithPath("address.roadName").description("도로명"),
                    fieldWithPath("address.mainBuildingNo").description("건물 본번"),
                    fieldWithPath("address.subBuildingNo").description("건물 부번").optional(),
                    fieldWithPath("address.longitude").description("경도").optional(),
                    fieldWithPath("address.latitude").description("위도").optional(),
                    fieldWithPath("categoryIds").description("카테고리 ID 리스트")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.LOCATION).description("생성된 게시글의 URI 입니다.")
                )
            ));
      }
    }

    @Nested
    @DisplayName("실패 테스트")
    public class Fail {

      @Test
      @DisplayName("사진이 없으면 게시글 저장은 실패한다.")
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
      @DisplayName("가게가 없으면 게시글 저장은 실패한다.")
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
      @DisplayName("가게의 주소가 없으면 게시글 저장은 실패한다.")
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
  @DisplayName("게시글 수정")
  class UpdatePost {

    @Nested
    @DisplayName("성공 테스트")
    public class Success {

      @Test
      @DisplayName("게시글 수정 성공")
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
                    cookieWithName(ACCESS_TOKEN.getName()).description("사용자 인증에 필요한 access token")
                ),
                pathParameters(
                    parameterWithName("id").description("수정하고자 하는 게시글 ID")
                ),
                requestParts(
                    partWithName("images").description("게시글 사진 리스트"),
                    partWithName("request").description("게시글 수정 Data")
                ),
                requestPartFields(
                    "request",
                    fieldWithPath("content").description("수정하려는 게시글 내용"),
                    fieldWithPath("store").description("수정하려는 가게 이름"),
                    fieldWithPath("store.storeName").description("상점 이름"),
                    fieldWithPath("address").description("수정하려는 가게 주소"),
                    fieldWithPath("address.addressName").description("전체 도로명 주소"),
                    fieldWithPath("address.region1depthName").description("지역명1"),
                    fieldWithPath("address.region2depthName").description("지역명2"),
                    fieldWithPath("address.region3depthName").description("지역명3"),
                    fieldWithPath("address.roadName").description("도로명"),
                    fieldWithPath("address.mainBuildingNo").description("건물 본번"),
                    fieldWithPath("address.subBuildingNo").description("건물 부번").optional(),
                    fieldWithPath("address.longitude").description("경도").optional(),
                    fieldWithPath("address.latitude").description("위도").optional(),
                    fieldWithPath("categoryIds").description("수정하려는 카테고리 ID 리스트")
                )));
      }
    }

    @Nested
    @DisplayName("실패 테스트")
    public class Fail {

      @Test
      @DisplayName("사진이 없으면 게시글 수정은 실패한다.")
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
      @DisplayName("가게가 없으면 게시글 수정은 실패한다.")
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
      @DisplayName("가게의 주소가 없으면 게시글 수정은 실패한다.")
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
  @DisplayName("게시글 삭제")
  class DeletePost {

    @Test
    @DisplayName("게시글 삭제에 성공한다.")
    void should_success_when_delete_post() throws Exception {
      mockingAuth();
      Long postId = 1L;

      doNothing().when(postService).deletePost((any(User.class)), anyLong());

      callDeletePostApi(postId)
          .andExpect(status().isNoContent())
          .andDo(document("post-delete",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description("사용자 인증에 필요한 access token")
              ),
              pathParameters(
                  parameterWithName("id").description("삭제하고자 하는 게시글 ID")
              )));
    }

    private ResultActions callDeletePostApi(Long postId) throws Exception {
      return mockMvc.perform(delete("/posts/{id}", postId)
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "token")))
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("유저 게시글 썸네일 목록 불러오기")
  class GetThumbnails {

    @Test
    @DisplayName("썸네일 목록 불러오기 성공")
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
                  fieldWithPath("[].createdAt").description("게시글 작성 시간")
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

  @Nested
  @DisplayName("유저 게시글 피드 목록 불러오기")
  class GetFeed {

    @Test
    @DisplayName("게시글 피드 목록 불러오기 성공")
    void should_success_when_getFeed() throws Exception {
      mockingAuth();

      PostFeedResponseDto mockResponse = PostFeedResponseDto.builder()
          .nickname("홍길동")
          .thumbnailPath("ThumbnailPath")
          .followerCount(100)
          .photoPaths(List.of("PhotoPath1", "PhotoPath2"))
          .storeName("틈새라면 홍대점")
          .categories(List.of("일식"))
          .latitude(new BigDecimal(17.561))
          .longitude(new BigDecimal(18.9078))
          .content("학교 앞에 생겼는데 너무 맛있어요!")
          .clipCount(4)
          .clipStatus(false)
          .commentCount(51)
          .createdAt(LocalDateTime.now())
          .build();

      doReturn(List.of(mockResponse)).when(postService)
          .getFeed(any(User.class), any(Pageable.class));

      callGetFeedApi()
          .andExpect(status().isOk())
          .andExpect(jsonPath("[0].nickname").value("홍길동"))
          .andExpect(jsonPath("[0].thumbnailPath").value("ThumbnailPath"))
          .andExpect(jsonPath("[0].followerCount").value(100))
          .andExpect(jsonPath("[0].storeName").value("틈새라면 홍대점"))
          .andExpect(jsonPath("[0].latitude").value(new BigDecimal(17.561)))
          .andExpect(jsonPath("[0].longitude").value(new BigDecimal(18.9078)))
          .andExpect(jsonPath("[0].content").value("학교 앞에 생겼는데 너무 맛있어요!"))
          .andExpect(jsonPath("[0].clipCount").value(4))
          .andExpect(jsonPath("[0].clipStatus").value(false))
          .andExpect(jsonPath("[0].commentCount").value(51))
          .andDo(document("post-feed",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getName()).description("사용자 인증에 필요한 access token")
              ),
              queryParameters(
                  parameterWithName("page").optional()
                      .description("불러오고 싶은 피드 목록 페이지 +\n(default: 0)"),
                  parameterWithName("size").optional()
                      .description("불러오고 싶은 피드 목록 크기 +\n(default: 4)")
              ),
              responseFields(
                  fieldWithPath("[].nickname").description("게시글 작성자 닉네임"),
                  fieldWithPath("[].thumbnailPath").description("게시글 작성자 썸네일 URI"),
                  fieldWithPath("[].followerCount").description("게시글 작성자 팔로워 수"),
                  fieldWithPath("[].photoPaths").description("게시글 사진 URI 목록"),
                  fieldWithPath("[].storeName").description("가게 이름"),
                  fieldWithPath("[].categories").description("카테고리 이름 목록"),
                  fieldWithPath("[].latitude").description("가게 위도"),
                  fieldWithPath("[].longitude").description("가게 경도"),
                  fieldWithPath("[].content").description("게시글 내용"),
                  fieldWithPath("[].clipCount").description("게시글 스크랩 수"),
                  fieldWithPath("[].clipStatus").description("게시글 스크랩 여부"),
                  fieldWithPath("[].commentCount").description("게시글 댓글 수"),
                  fieldWithPath("[].createdAt").description("게시글 작성 날짜")
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
  @DisplayName("본인 게시글을 제외한 모든 게시글 썸네일 조회 기능")
  class GetPostThumbnails {

    @Test
    @DisplayName("본인 게시글을 제외한 모든 게시글 썸네일 조회를 성공한다.")
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
                  cookieWithName(ACCESS_TOKEN.getName()).description("사용자 인증에 필요한 access token")
              ),
              queryParameters(
                  parameterWithName("page").optional()
                      .description("게시글 썸네일 목록 페이지 +\n(default: 0)"),
                  parameterWithName("size").optional()
                      .description("게시글 썸네일 목록 크기 +\n(default: 18)")
              ),
              responseFields(
                  fieldWithPath("[].postId").description("게시글 ID"),
                  fieldWithPath("[].thumbnailPath").description("게시글 썸네일 경로"),
                  fieldWithPath("[].createdAt").description("게시글 작성 시간")
              )));
    }

    private ResultActions callGetPostThumbnailsApi() throws Exception {
      return mockMvc.perform(get("/posts/thumbnails")
              .cookie(new Cookie(ACCESS_TOKEN.getName(), "token")))
          .andDo(print());
    }
  }
}
