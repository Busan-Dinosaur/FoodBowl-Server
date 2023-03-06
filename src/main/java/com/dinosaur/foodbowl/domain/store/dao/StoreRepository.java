package com.dinosaur.foodbowl.domain.store.dao;

import com.dinosaur.foodbowl.domain.store.entity.Store;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;

public interface StoreRepository extends Repository<Store, Long> {

  Store save(Store store);

  boolean existsByStoreName(String storeName);

  Store findByStoreName(String storeName);

  @EntityGraph(attributePaths = {"address"})
  List<Store> findStoresByStoreNameContaining(String storeName, Pageable pageable);
}
