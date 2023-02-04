package com.dinosaur.foodbowl.domain.store.dto.response;

import com.dinosaur.foodbowl.domain.address.dto.response.AddressResponseDto;
import com.dinosaur.foodbowl.domain.store.entity.Store;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StoreResponseDto {
  private String storeName;
  private AddressResponseDto address;

  public static StoreResponseDto toDto(Store store) {
    return StoreResponseDto.builder()
        .storeName(store.getStoreName())
        .address(AddressResponseDto.toDto(store.getAddress()))
        .build();
  }
}
