package com.dinosaur.foodbowl.domain.post.dto;

import com.dinosaur.foodbowl.domain.photo.entity.Photo;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.store.dto.StoreDto;
import com.dinosaur.foodbowl.domain.store.entity.Store;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.entity.User;
import jakarta.validation.constraints.NotEmpty;
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

  private String content;
  private StoreDto storeDto;
  private Long categoryId;
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
