package com.dinosaur.foodbowl.domain.post.application;

import static com.dinosaur.foodbowl.global.error.ErrorCode.POST_HAS_NOT_IMAGE;
import static com.dinosaur.foodbowl.global.error.ErrorCode.POST_NOT_WRITER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.address.dto.requset.AddressRequestDto;
import com.dinosaur.foodbowl.domain.category.entity.Category.CategoryType;
import com.dinosaur.foodbowl.domain.comment.entity.Comment;
import com.dinosaur.foodbowl.domain.photo.entity.Photo;
import com.dinosaur.foodbowl.domain.post.dto.request.PostCreateRequestDto;
import com.dinosaur.foodbowl.domain.post.dto.request.PostUpdateRequestDto;
import com.dinosaur.foodbowl.domain.post.dto.response.PostFeedResponseDto;
import com.dinosaur.foodbowl.domain.post.dto.response.PostThumbnailResponse;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.store.dto.request.StoreRequestDto;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.error.BusinessException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

class PostServiceTest extends IntegrationTest {

    @Nested
    class 게시글_생성 {

        @Test
        public void 게시글_생성에_성공한다() {
            // given
            User user = userTestHelper.builder().build();
            StoreRequestDto storeRequestDto = postTestHelper.generateStoreDto();
            AddressRequestDto addressRequestDto = postTestHelper.generateAddressDto();
            List<MultipartFile> images = List.of(photoTestHelper.getImageFile());
            PostCreateRequestDto requestDto = postTestHelper.getPostCreateRequestDto(
                    storeRequestDto,
                    addressRequestDto
            );

            // when
            Long postId = postService.createPost(user, requestDto, images);
            em.flush();
            em.clear();

            // then
            Post post = postRepository.getReferenceById(postId);
            assertThat(post).isNotNull();
            assertThat(post.getStore().getStoreName()).isEqualTo(storeRequestDto.getStoreName());
            assertThat(post.getStore().getAddress().getAddressName()).isEqualTo(addressRequestDto.getAddressName());
            assertThat(post.getPhotos()).isNotEmpty();

            post.getPhotos().forEach(photoTestHelper::deleteTestFile);
        }
    }

    @Nested
    class 게시글_수정 {

        @Test
        public void 게시글_수정에_성공한다() {
            // given
            User user = userTestHelper.builder().build();
            Post before = postTestHelper.builder()
                    .content("before")
                    .thumbnail(thumbnailTestHelper.generateThumbnail())
                    .user(user)
                    .store(null)
                    .build();
            Photo beforePhoto = photoTestHelper.generatePhoto(before);

            em.flush();
            em.clear();

            StoreRequestDto storeRequestDto = postTestHelper.generateStoreDto();
            AddressRequestDto addressRequestDto = postTestHelper.generateAddressDto();
            List<MultipartFile> images = List.of(photoTestHelper.getImageFile());
            List<Long> categoryIds = List.of(CategoryType.샐러드.getId(), CategoryType.양식.getId());
            PostUpdateRequestDto requestDto = postTestHelper.getPostUpdateRequestDto(
                    storeRequestDto,
                    addressRequestDto,
                    categoryIds
            );

            // when
            postService.updatePost(user, before.getId(), requestDto, images);
            Post after = postRepository.getReferenceById(before.getId());

            // then
            assertThat(after.getContent()).isEqualTo(requestDto.getContent());
            assertThat(after.getStore().getStoreName()).isEqualTo(requestDto.getStore().getStoreName());
            assertThat(after.getStore().getAddress().getAddressName()).isEqualTo(
                    requestDto.getAddress().getAddressName());
            assertThat(after.getPhotos().size()).isEqualTo(1);
            assertThat(after.getThumbnail()).isNotNull();
            List<Long> afterCategoryIds = after.getPostCategories().stream()
                    .map(postCategory -> postCategory.getCategory().getId())
                    .toList();
            assertThat(afterCategoryIds.size()).isEqualTo(2);
            assertThat(afterCategoryIds).containsAll(categoryIds);

            photoTestHelper.deleteTestFile(beforePhoto);
            after.getPhotos().forEach(photoTestHelper::deleteTestFile);
        }

        @Test
        public void 게시글을_수정하면_썸네일과_사진은_삭제된다() {
            // given
            User user = userTestHelper.builder().build();
            Thumbnail beforeThumbnail = thumbnailTestHelper.generateThumbnail();
            Post before = postTestHelper.builder()
                    .content("before")
                    .thumbnail(beforeThumbnail)
                    .user(user)
                    .store(null)
                    .build();
            Photo beforePhoto1 = photoTestHelper.generatePhoto(before);
            Photo beforePhoto2 = photoTestHelper.generatePhoto(before);
            em.flush();
            em.clear();

            StoreRequestDto storeRequestDto = postTestHelper.generateStoreDto();
            AddressRequestDto addressRequestDto = postTestHelper.generateAddressDto();
            List<MultipartFile> images = List.of(photoTestHelper.getImageFile());
            PostUpdateRequestDto requestDto = postTestHelper.getPostUpdateRequestDto(
                    storeRequestDto,
                    addressRequestDto,
                    List.of(1L, 2L)
            );

            // when
            postService.updatePost(user, before.getId(), requestDto, images);
            Post after = postRepository.getReferenceById(before.getId());
            em.flush();
            em.clear();

            // then
            Optional<Thumbnail> deletedThumbnail = thumbnailRepository.findById(beforeThumbnail.getId());
            assertThat(deletedThumbnail).isEmpty();
            Optional<Photo> deletedPhoto1 = photoRepository.findById(beforePhoto1.getId());
            assertThat(deletedPhoto1).isEmpty();
            Optional<Photo> deletedPhoto2 = photoRepository.findById(beforePhoto2.getId());
            assertThat(deletedPhoto2).isEmpty();

            photoTestHelper.deleteTestFile(beforePhoto1);
            photoTestHelper.deleteTestFile(beforePhoto2);
            after.getPhotos().forEach(photoTestHelper::deleteTestFile);
        }

        @Test
        public void 사진이_없으면_게시글_수정은_실패한다() {
            // given
            User user = userTestHelper.builder().build();
            Post before = postTestHelper.builder()
                    .content("before")
                    .thumbnail(null)
                    .user(user)
                    .store(null)
                    .build();
            PostUpdateRequestDto requestDto = postTestHelper.getValidPostUpdateRequestDto();

            // then
            assertThatThrownBy(() -> postService.updatePost(user, before.getId(), requestDto, Collections.emptyList()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(POST_HAS_NOT_IMAGE.getMessage());
        }

        @Test
        public void 게시글_작성자가_아니라면_예외가_발생한다() {
            // given
            User user = userTestHelper.builder().build();
            User another = userTestHelper.builder().build();
            Post before = postTestHelper.builder().user(user).build();
            PostUpdateRequestDto requestDto = postTestHelper.getValidPostUpdateRequestDto();

            // then
            assertThatThrownBy(() -> postService.updatePost(
                    another,
                    before.getId(),
                    requestDto,
                    List.of(photoTestHelper.getImageFile())
            )).isInstanceOf(BusinessException.class).hasMessageContaining(POST_NOT_WRITER.getMessage());
        }

        @Test
        public void 사진이_없으면_게시글_수정에_실패한다() {
            // given
            User user = userTestHelper.builder().build();
            Post before = postTestHelper.builder().user(user).build();
            PostUpdateRequestDto requestDto = postTestHelper.getValidPostUpdateRequestDto();

            // then
            assertThatThrownBy(() -> postService.updatePost(
                    user,
                    before.getId(),
                    requestDto,
                    Collections.emptyList()
            )).isInstanceOf(BusinessException.class).hasMessageContaining(POST_HAS_NOT_IMAGE.getMessage());
        }
    }

    @Nested
    class 게시글_삭제 {

        @Test
        void 게시글을_삭제하면_댓글_썸네일_사진도_함께_삭제된다() {
            User user = userTestHelper.builder().build();
            Thumbnail thumbnail = thumbnailTestHelper.generateThumbnail();
            Post post = postTestHelper.builder()
                    .user(user)
                    .thumbnail(thumbnail)
                    .build();
            Comment comment = commentTestHelper.builder().post(post).user(user).build();
            Photo photo1 = photoTestHelper.generatePhoto(post);
            Photo photo2 = photoTestHelper.generatePhoto(post);

            em.flush();
            em.clear();

            postService.deletePost(user, post.getId());

            Optional<Post> deletedPost = postRepository.findById(post.getId());
            assertThat(deletedPost).isEmpty();
            Optional<Thumbnail> deletedThumbnail = thumbnailRepository.findById(thumbnail.getId());
            assertThat(deletedThumbnail).isEmpty();
            Optional<Comment> deletedComment = commentRepository.findById(comment.getId());
            assertThat(deletedComment).isEmpty();
            Optional<Photo> deletedPhoto1 = photoRepository.findById(photo1.getId());
            assertThat(deletedPhoto1).isEmpty();
            Optional<Photo> deletedPhoto2 = photoRepository.findById(photo2.getId());
            assertThat(deletedPhoto2).isEmpty();

            photoTestHelper.deleteTestFile(photo1);
            photoTestHelper.deleteTestFile(photo2);

        }

        @Test
        void 게시글_작성자가_아니라면_예외가_발생한다() {
            User user = userTestHelper.builder().build();
            Post post = postTestHelper.builder().build();

            assertThatThrownBy(() -> postService.deletePost(user, post.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(POST_NOT_WRITER.getMessage());
        }
    }

    class 게시글_썸네일_조회 {

        @Test
        void 게시글_썸네일_목록을_조회한다() {
            User user = userTestHelper.builder().build();

            for (int i = 0; i < 5; i++) {
                postTestHelper.builder().user(user).content("test" + i).build();
            }

            Pageable pageable = PageRequest.of(1, 2, Sort.by("id").descending());
            List<PostThumbnailResponse> response = postService.getWrittenPostThumbnails(user.getId(), pageable);

            assertThat(response.size()).isEqualTo(2);
        }
    }

    @Nested
    class 게시글_피드_조회 {

        private User user;
        private Post post;

        @BeforeEach
        void setUp() {
            user = userTestHelper.builder().build();
            User user2 = userTestHelper.builder().build();

            followTestHelper.builder().following(user2).follower(user).build();

            post = postTestHelper.builder().user(user2).content("테스트 게시글").build();

            photoTestHelper.builder().post(post).build();
            photoTestHelper.builder().post(post).build();

            postCategoryTestHelper.builder().post(post).build();
            postCategoryTestHelper.builder().post(post).build();

            clipTestHelper.builder().post(post).build();
            clipTestHelper.builder().post(post).build();

            commentTestHelper.builder().post(post).build();
            commentTestHelper.builder().post(post).build();
        }

        @Test
        void 팔로우_하고있는_유저의_게시글만_조회한다() {
            em.flush();
            em.clear();

            Pageable pageable = PageRequest.of(0, 3, Sort.by("id").descending());

            List<PostFeedResponseDto> response = postService.getFeed(user, pageable);

            assertThat(response).hasSize(1);
            assertThat(response.get(0).getContent()).isEqualTo("테스트 게시글");
            assertThat(response.get(0).getFollowerCount()).isEqualTo(1);
            assertThat(response.get(0).getPhotoPaths()).hasSize(2);
            assertThat(response.get(0).getCategories()).hasSize(2);
            assertThat(response.get(0).getClipCount()).isEqualTo(2);
            assertThat(response.get(0).getCommentCount()).isEqualTo(2);
        }
    }

    @Nested
    class 특정_사용자_게시글_제외_모든_게시글_썸네일_조회 {

        @Test
        void 게시글_썸네일_목록을_조회한다() {
            //given
            User user1 = userTestHelper.builder().build();
            User user2 = userTestHelper.builder().build();
            User user3 = userTestHelper.builder().build();

            postTestHelper.builder().user(user1).build();
            Post post1 = postTestHelper.builder().user(user2).build();
            Post post2 = postTestHelper.builder().user(user3).build();

            Pageable pageable = PageRequest.of(0, 2, Sort.by("id").descending());

            //when
            List<PostThumbnailResponse> result = postService.getPostThumbnails(user1, pageable);

            //then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(PostThumbnailResponse::postId).containsExactly(post2.getId(), post1.getId());
            assertThat(result).extracting(PostThumbnailResponse::thumbnailPath)
                    .containsExactly(post2.getThumbnail().getPath(), post1.getThumbnail().getPath());
        }
    }
}
