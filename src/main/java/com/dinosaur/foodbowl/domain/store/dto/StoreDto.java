package com.dinosaur.foodbowl.domain.store.dto;

import static com.dinosaur.foodbowl.domain.store.entity.Store.MAX_STORE_NAME_LENGTH;

import com.dinosaur.foodbowl.domain.address.dto.AddressDto;
import com.dinosaur.foodbowl.domain.category.entity.Category;
import com.dinosaur.foodbowl.domain.store.entity.Store;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class StoreDto{
  @NotNull
  @Length(max = MAX_STORE_NAME_LENGTH)
  private String storeName;

  public Store toEntity(Category category, AddressDto addressDto) {
    return Store.builder()
        .address(addressDto.toEntity())
        .category(category)
        .storeName(storeName)
        .build();
  }
}
