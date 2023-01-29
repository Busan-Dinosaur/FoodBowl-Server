package com.dinosaur.foodbowl.domain.post.api;

import static com.dinosaur.foodbowl.global.config.security.JwtTokenProvider.ACCESS_TOKEN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.address.dto.AddressDto;
import com.dinosaur.foodbowl.domain.category.entity.Category.CategoryType;
import com.dinosaur.foodbowl.domain.post.dto.PostCreateRequestDto;
import com.dinosaur.foodbowl.domain.post.dto.PostCreateResponseDto;
import com.dinosaur.foodbowl.domain.store.dto.StoreDto;
import com.dinosaur.foodbowl.domain.user.dto.request.SignUpRequestDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
      doReturn(me).when(authUtil).getUserByJWT();
      doReturn(userId).when(authUtil).getUserIdByJWT();
    }


    @Nested
    @DisplayName("성공 테스트")
    public class Success {

      @Test
      @DisplayName("게시글 저장 성공")
      void test() throws Exception {
        StoreDto storeDto = postTestHelper.generateStoreDto();
        AddressDto addressDto = postTestHelper.generateAddressDto();
        PostCreateRequestDto requestDto = postTestHelper.getPostCreateRequestDto(storeDto, addressDto);
        String requestToJason = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
            requestToJason.getBytes(StandardCharsets.UTF_8));

        doReturn(1L).when(postService).createPost(any(User.class), any(), any());

        callCreatePostApi(request)
            .andExpect(status().isCreated());
      }
    }

    @Nested
    @DisplayName("실패 테스트")
    public class Fail {

      @Test
      @DisplayName("사진이 없으면 게시글 저장은 실패한다.")
      void should_fail_when_without_image() throws Exception {
        StoreDto storeDto = postTestHelper.generateStoreDto();
        AddressDto addressDto = postTestHelper.generateAddressDto();
        PostCreateRequestDto requestDto = postTestHelper.getPostCreateRequestDto(storeDto, addressDto);
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
        AddressDto addressDto = postTestHelper.generateAddressDto();
        PostCreateRequestDto requestDto = postTestHelper.getPostCreateRequestDto(null, addressDto);
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
        StoreDto storeDto = postTestHelper.generateStoreDto();
        PostCreateRequestDto requestDto = postTestHelper.getPostCreateRequestDto(storeDto, null);
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
              .cookie(new Cookie(ACCESS_TOKEN, userToken)))
          .andDo(print());
    }

    private ResultActions callCreatePostApiWithoutImage(MockMultipartFile request)
        throws Exception {
      return mockMvc.perform(multipart("/posts")
              .file(request)
              .contentType(MediaType.MULTIPART_FORM_DATA)
              .accept(MediaType.APPLICATION_JSON)
              .cookie(new Cookie(ACCESS_TOKEN, userToken)))
          .andDo(print());
    }
  }

}