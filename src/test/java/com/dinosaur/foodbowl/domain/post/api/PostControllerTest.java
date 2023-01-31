package com.dinosaur.foodbowl.domain.post.api;

import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.ACCESS_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.address.dto.AddressRequestDto;
import com.dinosaur.foodbowl.domain.post.dto.PostCreateRequestDto;
import com.dinosaur.foodbowl.domain.store.dto.StoreRequestDto;
import com.dinosaur.foodbowl.domain.user.entity.Role.RoleType;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;

public class PostControllerTest extends IntegrationTest {

  @Autowired
  ObjectMapper objectMapper;

  @Nested
  @DisplayName("게시글 생성")
  class CreatePost {

    private final Long userId = 1L;
    private final String userToken = jwtTokenProvider.createAccessToken(userId, RoleType.ROLE_회원);

    @BeforeEach
    void setup() {
      User me = User.builder().build();
      ReflectionTestUtils.setField(me, "id", userId);
      doReturn(me).when(userFindDao).findById(anyLong());
    }


    @Nested
    @DisplayName("성공 테스트")
    public class Success {

      @Test
      @DisplayName("올바른 요청이면 게시글 생성은 성공한다.")
      void should_success_create_post() throws Exception {
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
                requestParts(
                    partWithName("images").description("게시글 사진"),
                    partWithName("request").description("게시글 내용, 상점, 주소, 카테고리 id")
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
      @DisplayName("가게의 주소 없으면 게시글 저장은 실패한다.")
      void should_fail_when_without_address() throws Exception {
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
              .file(photoTestHelper.getPhotoFile())
              .file(request)
              .contentType(MediaType.MULTIPART_FORM_DATA)
              .accept(MediaType.APPLICATION_JSON)
              .cookie(new Cookie(ACCESS_TOKEN.getName(), userToken)))
          .andDo(print());
    }

    private ResultActions callCreatePostApiWithoutImage(MockMultipartFile request)
        throws Exception {
      return mockMvc.perform(multipart("/posts")
              .file(request)
              .contentType(MediaType.MULTIPART_FORM_DATA)
              .accept(MediaType.APPLICATION_JSON)
              .cookie(new Cookie(ACCESS_TOKEN.getName(), userToken)))
          .andDo(print());
    }
  }

}