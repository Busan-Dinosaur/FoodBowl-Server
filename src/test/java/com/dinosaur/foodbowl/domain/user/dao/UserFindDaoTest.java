package com.dinosaur.foodbowl.domain.user.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.error.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserFindDaoTest extends IntegrationTest {

  @Nested
  @DisplayName("로그인 아이디로 유저 찾기")
  class FindByLoginId {

    @Test
    @DisplayName("해당 로그인 아이디를 가진 유저가 존재하면 유저를 반환한다.")
    void should_returnUser_when_loginIdExist() {
      String loginId = "testLoginId";
      User user = userTestHelper.builder().loginId(loginId).build();

      User findUser = userFindDao.findByLoginId(loginId);

      assertThat(user).isEqualTo(findUser);
    }

    @Test
    @DisplayName("해당 로그인 아이디를 가진 유저가 존재하지 않으면 예외가 발생한다.")
    void should_throwException_when_loginIdNotExist() {
      String loginId = "testLoginId";

      assertThatThrownBy(() -> userFindDao.findByLoginId(loginId))
          .isInstanceOf(BusinessException.class);
    }
  }
}
