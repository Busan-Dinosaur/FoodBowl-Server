package com.dinosaur.foodbowl.domain.user.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.domain.user.entity.role.Role;
import com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType;
import com.dinosaur.foodbowl.global.dao.RepositoryTest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RoleRepositoryTest extends RepositoryTest {

  @Autowired
  private RoleRepository roleRepository;

  @Nested
  class RoleTypeEnumTest {

    @Test
    void should_allTypeExist_when_givenRoleTypeEnum() {
      List<Role> allRoles = roleRepository.findAll();
      List<Role> allRoleTypes = Arrays.stream(RoleType.values())
          .map(Role::getRoleBy)
          .collect(Collectors.toList());

      assertThat(allRoles).containsSequence(allRoleTypes);
    }
  }
}