package com.dinosaur.foodbowl.domain.blame.dao;

import com.dinosaur.foodbowl.domain.blame.entity.Blame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlameRepository extends JpaRepository<Blame, Long> {

}
