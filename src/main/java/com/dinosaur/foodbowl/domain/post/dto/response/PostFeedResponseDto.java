package com.dinosaur.foodbowl.domain.post.dto.response;

import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostFeedResponseDto {

  private final String nickname;
  private final String thumbnailPath;
  private final int followerCount;
  private final List<String> photoPaths;
  private final String storeName;
  private final List<String> categories;
  private final BigDecimal latitude;
  private final BigDecimal longitude;
  private final String content;
  private final int clipCount;
  private final boolean clipStatus;
  private final int commentCount;
  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private final LocalDateTime createdAt;

  public static PostFeedResponseDto of(final Post post, final User user) {
    return PostFeedResponseDto.builder()
        .nickname(post.getUser().getNickname().getNickname())
        .thumbnailPath(post.getUser().getThumbnailURL().orElse(null))
        .followerCount(post.getUser().getFollowerSize())
        .photoPaths(post.getPhotoPaths())
        .storeName(post.getStore().getStoreName())
        .categories(post.getCategoryNames())
        .latitude(post.getStore().getAddress().getLatitude())
        .longitude(post.getStore().getAddress().getLongitude())
        .content(post.getContent())
        .clipCount(post.getClipSize())
        .clipStatus(post.isCliped(user))
        .commentCount(post.getCommentSize())
        .createdAt(post.getCreatedAt())
        .build();
  }
}
