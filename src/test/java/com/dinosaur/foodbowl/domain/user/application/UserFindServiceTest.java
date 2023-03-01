package com.dinosaur.foodbowl.domain.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.error.BusinessException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserFindServiceTest extends IntegrationTest {

  @Nested
  class 아이디로_유저_조회 {

    @Test
    void 로그인_아이디가_존재하면_유저를_조회한다() {
      String loginId = "testLoginId";
      User user = userTestHelper.builder().loginId(loginId).build();

      User findUser = userFindService.findByLoginId(loginId);

      assertThat(user).isEqualTo(findUser);
    }

    @Test
    void 로그인_아이디가_존재하지_않으면_예외가_발생한다() {
      String loginId = "testLoginId";

      assertThatThrownBy(() -> userFindService.findByLoginId(loginId))
          .isInstanceOf(BusinessException.class);
    }
  }
}
