package com.dinosaur.foodbowl.domain.user.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserRepositoryTest extends IntegrationTest {

  private static final int MAX_LOGIN_ID_LENGTH = 40;
  private static final int MAX_PASSWORD_LENGTH = 512;
  private static final int MAX_NICKNAME_LENGTH = 40;
  private static final int MAX_INTRODUCE_LENGTH = 255;

  private User user;

  @BeforeEach
  void setUpUser() {
    user = userTestHelper.generateUser();
  }

  private static String getRandomUUIDLengthWith(int length) {
    String randomString = UUID.randomUUID()
        .toString();
    length = Math.min(length, randomString.length());
    return randomString.substring(0, length);
  }

  @Nested
  class UniqueColumnTest {

    @Test
    void should_throwException_when_uniqueColumnIsDuplicate() {
      assertThatThrownBy(this::generateLoginIdDuplicateUser);
      assertThatThrownBy(this::generateNicknameDuplicateUser);
    }

    private void generateLoginIdDuplicateUser() {
      User duplicateUser = User.builder()
          .loginId(user.getLoginId())
          .nickname(getRandomUUIDLengthWith(MAX_NICKNAME_LENGTH))
          .password(getRandomUUIDLengthWith(MAX_PASSWORD_LENGTH))
          .introduce(getRandomUUIDLengthWith(MAX_INTRODUCE_LENGTH))
          .build();
      userRepository.save(duplicateUser);
    }

    private void generateNicknameDuplicateUser() {
      User duplicateUser = User.builder()
          .loginId(getRandomUUIDLengthWith(MAX_LOGIN_ID_LENGTH))
          .nickname(user.getNickname())
          .password(getRandomUUIDLengthWith(MAX_PASSWORD_LENGTH))
          .introduce(getRandomUUIDLengthWith(MAX_INTRODUCE_LENGTH))
          .build();
      userRepository.save(duplicateUser);
    }

    @Test
    void should_createSuccessfully_when_nonUniqueColumnIsDuplicate() {
      assertThatNoException().isThrownBy(this::generatePasswordDuplicateUser);
      assertThatNoException().isThrownBy(this::generateIntroduceDuplicateUser);
    }

    private void generatePasswordDuplicateUser() {
      User duplicateUser = User.builder()
          .loginId(getRandomUUIDLengthWith(MAX_LOGIN_ID_LENGTH))
          .nickname(getRandomUUIDLengthWith(MAX_NICKNAME_LENGTH))
          .password(user.getPassword())
          .introduce(getRandomUUIDLengthWith(MAX_INTRODUCE_LENGTH))
          .build();
      userRepository.save(duplicateUser);
    }

    private void generateIntroduceDuplicateUser() {
      User duplicateUser = User.builder()
          .loginId(getRandomUUIDLengthWith(MAX_LOGIN_ID_LENGTH))
          .nickname(getRandomUUIDLengthWith(MAX_NICKNAME_LENGTH))
          .password(getRandomUUIDLengthWith(MAX_PASSWORD_LENGTH))
          .introduce(user.getIntroduce())
          .build();
      userRepository.save(duplicateUser);
    }
  }

  @Nested
  class UserRoleTest {

    @Test
    void should_assignUserRoleCorrectly_when_saveUser() {
      em.flush();
      em.clear();

      user = userRepository.findById(user.getId()).orElseThrow();

      assertThat(user.containsRole(RoleType.ROLE_회원)).isTrue();
    }

    @Test
    void should_nothingHappens_when_duplicateRoles() {
      em.flush();
      em.clear();

      user = userRepository.findById(user.getId()).orElseThrow();
      user.assignRole(RoleType.ROLE_회원);

      assertThat(user.containsRole(RoleType.ROLE_회원)).isTrue();
      assertThatNoException().isThrownBy(() -> userRoleRepository.findByUser(user));
      assertThat(userRoleRepository.findByUser(user)).isNotEmpty();
    }
  }

  @Nested
  class UserDeleteTest {

    @Test
    void should_deleteAllUserInfo_when_deleteMySelf() {
      User userWithThumbnail = userTestHelper.generateUser();

      whenDeleteUser(userWithThumbnail);

      assertThat(userRepository.findById(userWithThumbnail.getId())).isEmpty();
      assertThat(userRoleRepository.findByUser(userWithThumbnail)).isEmpty();
      assertThat(thumbnailRepository.findByPath(getUserThumbnailPath(userWithThumbnail))).isEmpty();
    }

    private void whenDeleteUser(User userWithThumbnail) {
      userRepository.delete(userWithThumbnail);
      em.flush();
      em.clear();
    }

    private String getUserThumbnailPath(User userWithThumbnail) {
      return userWithThumbnail.getThumbnailURL()
          .orElseThrow();
    }
  }

  @Nested
  @DisplayName("유저 프로필 업데이트")
  class UserUpdateProfileTest {

    @Test
    @DisplayName("썸네일이 null일 경우 썸네일은 바뀌어선 안된다.")
    void should_notChangeThumbnail_when_nullThumbnail() {
      User beforeUser = userTestHelper.generateUser();
      String newIntroduce = "newIntroduce";

      whenUpdateUser(beforeUser, null, newIntroduce);

      checkUserUpdated(beforeUser, beforeUser.getThumbnailURL(), newIntroduce);
      checkThumbnailUpdated(beforeUser);
    }

    @Test
    @DisplayName("소개가 null일 경우 소개는 바뀌어선 안된다.")
    void should_notChangeThumbnail_when_nullIntroduce() {
      User beforeUser = userTestHelper.generateUser();
      Thumbnail newThumbnail = thumbnailTestHelper.generateThumbnail();

      whenUpdateUser(beforeUser, newThumbnail, null);

      checkUserUpdated(beforeUser, Optional.of(newThumbnail.getPath()), beforeUser.getIntroduce());
      checkThumbnailUpdated(beforeUser);
    }

    @Test
    @DisplayName("썸네일과 소개가 null일 경우 둘 다 바뀌어선 안된다.")
    void should_notChangeThumbnail_when_nullEverything() {
      User beforeUser = userTestHelper.generateUser();

      whenUpdateUser(beforeUser, null, null);

      checkUserUpdated(beforeUser, beforeUser.getThumbnailURL(), beforeUser.getIntroduce());
      checkThumbnailUpdated(beforeUser);
    }

    private void checkUserUpdated(User beforeUser, Optional<String> newThumbnailPath,
        String newIntroduce) {
      User findUser = userRepository.findById(beforeUser.getId()).orElseThrow();
      assertThat(findUser).isEqualTo(beforeUser);
      assertThat(findUser.getIntroduce()).isEqualTo(newIntroduce);
      assertThat(findUser.getLoginId()).isEqualTo(beforeUser.getLoginId());
      assertThat(findUser.getNickname()).isEqualTo(beforeUser.getNickname());
      assertThat(findUser.getPassword()).isEqualTo(beforeUser.getPassword());
      assertThat(findUser.getThumbnailURL()).hasValue(newThumbnailPath.orElseThrow());
    }

    private void checkThumbnailUpdated(User userWithThumbnail) {
      String userThumbnailPath = getUserThumbnailPath(userWithThumbnail);
      assertThat(thumbnailRepository.findByPath(userThumbnailPath)).isNotEmpty();
      assertThat(thumbnailRepository.findByPath(userThumbnailPath).orElseThrow().getPath())
          .isEqualTo(userThumbnailPath);
    }

    private void whenUpdateUser(User user, Thumbnail thumbnail, String newIntroduce) {
      user.updateProfile(thumbnail, newIntroduce);
      em.flush();
      em.clear();
    }

    private String getUserThumbnailPath(User userWithThumbnail) {
      return userWithThumbnail.getThumbnailURL()
          .orElseThrow();
    }
  }

  @Nested
  @DisplayName("유저 팔로잉")
  class UserFollowTest {

    @Test
    @DisplayName("팔로우를 여러번 해도 같은 사람이면 한 번만 들어가야한다.")
    void should_once_when_duplicateFollow() {
      User me = userTestHelper.generateUser();
      User other1 = userTestHelper.generateUser();
      User other2 = userTestHelper.generateUser();

      me.follow(other1);
      me.follow(other2);
      me.follow(other2);
      me.follow(other2);

      em.flush();
      em.clear();

      long followingCount = followRepository.countByFollower(me);
      assertThat(followingCount).isEqualTo(2);
    }
  }
}