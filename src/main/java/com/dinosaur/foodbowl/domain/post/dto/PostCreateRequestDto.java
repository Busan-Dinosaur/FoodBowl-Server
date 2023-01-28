package com.dinosaur.foodbowl.domain.post.dto;

import com.dinosaur.foodbowl.domain.photo.entity.Photo;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.store.dto.StoreDto;
import com.dinosaur.foodbowl.domain.store.entity.Store;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PostCreateRequestDto {

  @NotNull
  private String content;
  @NotNull
  private StoreDto storeDto;
  @NotNull
  private Long categoryId;
  @Valid
  @Size(min = 1, message = "게시글의 사진은 최소 1장 이상이어야 합니다.")
  private List<MultipartFile> photoFiles;

  public Post toEntity(User user, Store store, List<Photo> photos, Thumbnail thumbnail) {
    return Post.builder()
        .user(user)
        .thumbnail(thumbnail)
        .store(store)
        .photos(photos)
        .content(content)
        .build();
  }
}
