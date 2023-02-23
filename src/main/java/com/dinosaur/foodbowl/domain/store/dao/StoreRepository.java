package com.dinosaur.foodbowl.domain.store.dao;

import com.dinosaur.foodbowl.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {

  boolean existsByStoreName(String storeName);

  Store findByStoreName(String storeName);
}
