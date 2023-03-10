package com.dinosaur.foodbowl.domain.post.dto.request;

import com.dinosaur.foodbowl.domain.address.dto.requset.AddressRequestDto;
import com.dinosaur.foodbowl.domain.photo.entity.Photo;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.store.dto.request.StoreRequestDto;
import com.dinosaur.foodbowl.domain.store.entity.Store;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostCreateRequestDto {

  @NotNull
  private String content;

  @NotNull
  private StoreRequestDto store;

  @NotNull
  private AddressRequestDto address;

  @Valid
  @NotNull
  private List<Long> categoryIds;


  public Post toEntity(User user, Store store, Thumbnail thumbnail) {
    return Post.builder()
        .user(user)
        .thumbnail(thumbnail)
        .store(store)
        .content(content)
        .postCategories(new ArrayList<>())
        .build();
  }
}
