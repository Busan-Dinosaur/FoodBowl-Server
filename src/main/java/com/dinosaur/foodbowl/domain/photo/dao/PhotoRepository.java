package com.dinosaur.foodbowl.domain.photo.dao;

import com.dinosaur.foodbowl.domain.photo.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

  @Override
  boolean existsById(Long aLong);
}
