package com.dinosaur.foodbowl.domain.thumbnail.dao;

import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThumbnailRepository extends JpaRepository<Thumbnail, Long> {

  Optional<Thumbnail> findByPath(String path);
}
