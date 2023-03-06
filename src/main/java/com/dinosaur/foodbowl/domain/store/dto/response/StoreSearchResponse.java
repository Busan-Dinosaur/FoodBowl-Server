package com.dinosaur.foodbowl.domain.store.dto.response;

import com.dinosaur.foodbowl.domain.store.entity.Store;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record StoreSearchResponse(
    Long storeId, String storeName, long postCount, BigDecimal latitude, BigDecimal longitude) {

  public static StoreSearchResponse from(final Store store, final long postCount) {
    return StoreSearchResponse.builder()
        .storeId(store.getId())
        .storeName(store.getStoreName())
        .postCount(postCount)
        .latitude(store.getAddress().getLatitude())
        .longitude(store.getAddress().getLongitude())
        .build();
  }
}
