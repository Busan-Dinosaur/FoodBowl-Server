package com.dinosaur.foodbowl.domain.store.dao;

import com.dinosaur.foodbowl.domain.address.dto.AddressRequestDto;
import com.dinosaur.foodbowl.domain.category.entity.Category;
import com.dinosaur.foodbowl.domain.store.dto.StoreRequestDto;
import com.dinosaur.foodbowl.domain.store.entity.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StoreFindDao {

  private final StoreRepository storeRepository;

  public Store findStoreByName(StoreRequestDto store, AddressRequestDto address) {
    if (storeRepository.existsByStoreName(store.getStoreName())) {
      return storeRepository.findByStoreName(store.getStoreName());
    }
    return store.toEntity(address);
  }
}
