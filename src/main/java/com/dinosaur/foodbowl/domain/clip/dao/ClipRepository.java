package com.dinosaur.foodbowl.domain.clip.dao;

import com.dinosaur.foodbowl.domain.clip.entity.Clip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClipRepository extends JpaRepository<Clip, Long> {

}
