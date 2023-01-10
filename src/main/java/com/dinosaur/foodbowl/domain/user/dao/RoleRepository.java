package com.dinosaur.foodbowl.domain.user.dao;

import com.dinosaur.foodbowl.domain.user.entity.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
