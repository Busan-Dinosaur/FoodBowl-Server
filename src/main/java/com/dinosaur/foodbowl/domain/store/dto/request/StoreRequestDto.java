package com.dinosaur.foodbowl.domain.store.dto.request;

import static com.dinosaur.foodbowl.domain.store.entity.Store.MAX_STORE_NAME_LENGTH;

import com.dinosaur.foodbowl.domain.address.dto.requset.AddressRequestDto;
import com.dinosaur.foodbowl.domain.store.entity.Store;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreRequestDto {

  @NotNull
  @Length(max = MAX_STORE_NAME_LENGTH)
  private String storeName;

  public Store toEntity(AddressRequestDto addressRequestDto) {
    return Store.builder()
        .address(addressRequestDto.toEntity())
        .storeName(storeName)
        .build();
  }
}
