package com.dinosaur.foodbowl.domain.store.api;

import com.dinosaur.foodbowl.domain.store.application.StoreService;
import com.dinosaur.foodbowl.domain.store.dto.response.StoreSearchResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
public class StoreController {

  private final StoreService storeService;

  @GetMapping
  public ResponseEntity<List<StoreSearchResponse>> searchStoresByName(
      @RequestParam("name") String storeName,
      @PageableDefault(size = 15, sort = "storeName", direction = Direction.ASC) Pageable pageable
  ) {
    final List<StoreSearchResponse> response = storeService.searchStoresByName(storeName, pageable);

    return ResponseEntity.ok(response);
  }
}
