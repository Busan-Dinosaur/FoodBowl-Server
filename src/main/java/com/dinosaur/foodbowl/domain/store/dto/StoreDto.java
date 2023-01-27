package com.dinosaur.foodbowl.domain.store.dto;

import static com.dinosaur.foodbowl.domain.store.entity.Store.MAX_STORE_NAME_LENGTH;

import com.dinosaur.foodbowl.domain.address.dto.AddressDto;
import com.dinosaur.foodbowl.domain.category.entity.Category;
import com.dinosaur.foodbowl.domain.store.entity.Store;
import org.hibernate.validator.constraints.Length;

public class StoreDto {

  private AddressDto addressDto;
  private Category category;
  @Length(max = MAX_STORE_NAME_LENGTH)
  private String storeName;

  public Store toEntity() {
    return Store.builder()
        .address(addressDto.toEntity())
        .category(category)
        .storeName(storeName)
        .build();
  }
}
