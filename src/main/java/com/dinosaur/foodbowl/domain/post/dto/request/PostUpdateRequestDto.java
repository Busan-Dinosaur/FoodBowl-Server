package com.dinosaur.foodbowl.domain.post.dto.request;

import com.dinosaur.foodbowl.domain.address.dto.requset.AddressRequestDto;
import com.dinosaur.foodbowl.domain.store.dto.request.StoreRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostUpdateRequestDto {

  @NotNull
  private Long postId;
  @NotNull
  private String content;
  @NotNull
  private StoreRequestDto store;
  @NotNull
  private AddressRequestDto address;
  @Valid
  @NotNull
  private List<Long> categoryIds;
  @Valid
  @NotNull
  private List<MultipartFile> images;

}
