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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostCreateRequestDto {

  private Long userId;
  private String content;
  private StoreDto storeDto;
  private List<MultipartFile> photoFiles;

  public Post toEntity(User user, Thumbnail thumbnail, List<Photo> photos) {
    return Post.builder()
        .user(user)
        .thumbnail(thumbnail)
        .store(storeDto.toEntity())
        .photos(photos)
        .content(content)
        .build();
  }
}
