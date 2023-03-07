package com.dinosaur.foodbowl.domain.post;

import com.dinosaur.foodbowl.domain.address.dto.requset.AddressRequestDto;
import com.dinosaur.foodbowl.domain.category.entity.Category.CategoryType;
import com.dinosaur.foodbowl.domain.post.dao.PostRepository;
import com.dinosaur.foodbowl.domain.post.dto.request.PostCreateRequestDto;
import com.dinosaur.foodbowl.domain.post.dto.request.PostUpdateRequestDto;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.store.StoreTestHelper;
import com.dinosaur.foodbowl.domain.store.dto.request.StoreRequestDto;
import com.dinosaur.foodbowl.domain.store.entity.Store;
import com.dinosaur.foodbowl.domain.thumbnail.ThumbnailTestHelper;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.UserTestHelper;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostTestHelper {

    @Autowired
    UserTestHelper userTestHelper;

    @Autowired
    ThumbnailTestHelper thumbnailTestHelper;

    @Autowired
    StoreTestHelper storeTestHelper;

    @Autowired
    PostRepository postRepository;

    private String getRandomUUIDLengthWith(int length) {
        String randomString = UUID.randomUUID().toString();
        length = Math.min(length, randomString.length());
        return randomString.substring(0, length);
    }

    public PostBuilder builder() {
        return new PostBuilder();
    }

    public final class PostBuilder {

        private User user;
        private Thumbnail thumbnail;
        private Store store;
        private String content;

        private PostBuilder() {
        }

        public PostBuilder user(User user) {
            this.user = user;
            return this;
        }

        public PostBuilder thumbnail(Thumbnail thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public PostBuilder store(Store store) {
            this.store = store;
            return this;
        }

        public PostBuilder content(String content) {
            this.content = content;
            return this;
        }

        public Post build() {
            return postRepository.save(Post.builder()
                    .user(user != null ? user : userTestHelper.builder().build())
                    .thumbnail(thumbnail != null ? thumbnail : thumbnailTestHelper.generateThumbnail())
                    .store(store != null ? store : storeTestHelper.builder().build())
                    .content(content != null ? content : getRandomUUIDLengthWith(100))
                    .build());
        }
    }

    public PostUpdateRequestDto getValidPostUpdateRequestDto() {
        return PostUpdateRequestDto.builder()
                .store(this.generateStoreDto())
                .address(this.generateAddressDto())
                .content("test")
                .categoryIds(List.of(CategoryType.전체.getId(), CategoryType.분식.getId()))
                .build();
    }

    public PostUpdateRequestDto getPostUpdateRequestDto(StoreRequestDto storeRequestDto,
            AddressRequestDto addressRequestDto, List<Long> categoryIds) {
        return PostUpdateRequestDto.builder()
                .store(storeRequestDto)
                .address(addressRequestDto)
                .content("test")
                .categoryIds(categoryIds)
                .build();
    }

    public PostCreateRequestDto getPostCreateRequestDto(StoreRequestDto storeRequestDto,
            AddressRequestDto addressRequestDto) {
        return PostCreateRequestDto.builder()
                .store(storeRequestDto)
                .address(addressRequestDto)
                .content("test")
                .categoryIds(List.of(CategoryType.전체.getId(), CategoryType.분식.getId()))
                .build();
    }

    public StoreRequestDto generateStoreDto() {
        return StoreRequestDto.builder()
                .storeName("test")
                .build();
    }

    public AddressRequestDto generateAddressDto() {
        return AddressRequestDto.builder()
                .addressName("부산광역시 부산대학로 1")
                .region1depthName("부산광역시")
                .region2depthName("금정구")
                .region3depthName("장전동")
                .roadName("부산대학로")
                .mainBuildingNo("1")
                .build();
    }
}
