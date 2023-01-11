/*
package com.dinosaur.foodbowl.domain.user;

import com.dinosaur.foodbowl.domain.user.dao.UserRepository;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.dao.RepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;

public class UserTestHelper extends RepositoryTest {

  @Autowired
  private UserRepository userRepository;

  private UserTestHelper.UserBuilder builder() {
    return new UserTestHelper.UserBuilder();
  }

  private final class UserBuilder {

    private User user1;
    private User user2;
    private User user;

    private User setUpUser(String loginId, String password, String nickname, String introduce) {
      User user = User.builder()
          .loginId(loginId)
          .password(password)
          .nickname(nickname)
          .introduce(introduce)
          .build();
      return userRepository.save(user);
    }

    private UserTestHelper.UserBuilder loginId(
        String loginId, String password, String nickname, String introduce
    ) {
      this.user1 = setUpUser(loginId, password, nickname, introduce);
      return this;
    }

    private UserTestHelper.UserBuilder setUpUserer(
        String loginId, String password, String nickname, String introduce
    ) {
      this.user2 = setUpUser(loginId, password, nickname, introduce);
      return this;
    }

    private UserTestHelper.UserBuilder doUser() {
      User user = User.builder()
          .user1(user1)
          .user2(user2)
          .build();
      this.user = userRepository.save(user);
      return this;
    }

    private User user() {
      return this.user;
    }
  }
}
*/
