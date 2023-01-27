package com.dinosaur.foodbowl.domain.user.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.UserTestHelper.UserBuilder;
import com.dinosaur.foodbowl.domain.user.entity.Role.RoleType;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserRepositoryTest extends IntegrationTest {

  private UserBuilder userBuilder;
  private User user;

  @BeforeEach
  void setUp() {
    userBuilder = userTestHelper.builder();
    user = userBuilder.build();
  }

  @Nested
  @DisplayName("유니크 컬럼 테스트")
  class UniqueColumnTest {

    @Test
    @DisplayName("유니크 컬럼에 중복이 발생하면 예외가 발생한다.")
    void should_throwException_when_uniqueColumnIsDuplicate() {
      assertThatThrownBy(() -> userBuilder.loginId(user.getLoginId()).build());
      assertThatThrownBy(() -> userBuilder.nickname(user.getNickname()).build());
    }

    @Test
    @DisplayName("유니크 하지 않은 컬럼에 중복이 발생하면 예외가 발생하지 않는다.")
    void should_createSuccessfully_when_nonUniqueColumnIsDuplicate() {
      assertThatNoException().isThrownBy(() -> userBuilder.password(user.getPassword()).build());
      assertThatNoException().isThrownBy(() -> userBuilder.introduce(user.getIntroduce()).build());
    }
  }

  @Nested
  @DisplayName("존재하는 컬럼 테스트")
  class ExistTest {

    @Test
    @DisplayName("로그인 아이디가 존재하면 true 반환한다.")
    void should_returnTrue_when_loginIdExist() {
      String loginId = user.getLoginId();

      boolean result = userRepository.existsByLoginId(loginId);

      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("로그인 아이디가 존재하지 않으면 false 반환한다.")
    void should_returnFalse_when_loginIdNotExist() {
      String loginId = "not-exist-loginId";

      boolean result = userRepository.existsByLoginId(loginId);

      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("닉네임이 존재하면 true 반환한다.")
    void should_returnTrue_when_nicknameExist() {
      String nickname = user.getNickname();

      boolean result = userRepository.existsByNickname(nickname);

      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("닉네임이 존재하지 않으면 false 반환한다.")
    void should_returnFalse_when_nicknameNotExist() {
      String nickname = "not-exist-nickname";

      boolean result = userRepository.existsByNickname(nickname);

      assertThat(result).isFalse();
    }
  }

  @Nested
  @DisplayName("회원 권한 테스트")
  class UserRoleTest {

    @Test
    @DisplayName("회원을 저장할 때 권한을 올바르게 가지고 있는다.")
    void should_assignUserRoleCorrectly_when_saveUser() {
      em.flush();
      em.clear();

      user = userRepository.findById(user.getId()).orElseThrow();

      assertThat(user.containsRole(RoleType.ROLE_회원)).isTrue();
    }

    @Test
    @DisplayName("회원은 중복되는 권한을 가지지 않는다.")
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
  @DisplayName("회원 삭제 테스트")
  class UserDeleteTest {

    @Test
    @DisplayName("회원 본인이 삭제 요청을 하면 회원과 관련된 모든 정보가 삭제된다.")
    void should_deleteAllUserInfo_when_deleteMySelf() {
      User userWithThumbnail = userBuilder.thumbnail(thumbnailTestHelper.generateThumbnail())
          .build();

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
      User beforeUser = userBuilder.thumbnail(thumbnailTestHelper.generateThumbnail()).build();
      String newIntroduce = "newIntroduce";

      whenUpdateUser(beforeUser, null, newIntroduce);

      checkUserUpdated(beforeUser, beforeUser.getThumbnailURL(), newIntroduce);
      checkThumbnailUpdated(beforeUser);
    }

    @Test
    @DisplayName("소개가 null일 경우 소개는 바뀌어선 안된다.")
    void should_notChangeThumbnail_when_nullIntroduce() {
      User beforeUser = userBuilder.thumbnail(thumbnailTestHelper.generateThumbnail()).build();
      Thumbnail newThumbnail = thumbnailTestHelper.generateThumbnail();

      whenUpdateUser(beforeUser, newThumbnail, null);

      checkUserUpdated(beforeUser, Optional.of(newThumbnail.getPath()), beforeUser.getIntroduce());
      checkThumbnailUpdated(beforeUser);
    }

    @Test
    @DisplayName("썸네일과 소개가 null일 경우 둘 다 바뀌어선 안된다.")
    void should_notChangeThumbnail_when_nullEverything() {
      User beforeUser = userBuilder.thumbnail(thumbnailTestHelper.generateThumbnail()).build();

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
      User me = userBuilder.thumbnail(thumbnailTestHelper.generateThumbnail()).build();
      User other1 = userBuilder.thumbnail(thumbnailTestHelper.generateThumbnail()).build();
      User other2 = userBuilder.thumbnail(thumbnailTestHelper.generateThumbnail()).build();

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
