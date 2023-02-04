package com.dinosaur.foodbowl.domain.post.dto.response;

import com.dinosaur.foodbowl.domain.photo.entity.Photo;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.store.dto.response.StoreResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostUpdateResponseDto {

  private Long postId;
  private String content;
  private StoreResponseDto store;
  private List<Long> categoryIds;
  private List<Photo> photos;
  private LocalDateTime updatedAt;

  public static PostUpdateResponseDto toDto(Post post) {
    return PostUpdateResponseDto.builder()
        .postId(post.getId())
        .content(post.getContent())
        .store(StoreResponseDto.toDto(post.getStore()))
        .categoryIds(post.getPostCategories().stream()
            .map(postCategory -> postCategory.getCategory().getId())
            .toList())
        .build();
  }
}
