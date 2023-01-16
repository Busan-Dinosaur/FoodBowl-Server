package com.dinosaur.foodbowl.domain.user.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.dinosaur.foodbowl.domain.thumbnail.dao.ThumbnailRepository;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType;
import com.dinosaur.foodbowl.global.dao.RepositoryTest;
import com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailUtil;
import com.dinosaur.foodbowl.global.util.thumbnail.file.ThumbnailFileUtil;
import jakarta.persistence.EntityManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

class UserRepositoryTest extends RepositoryTest {

  private static final int MAX_LOGIN_ID_LENGTH = 40;
  private static final int MAX_PASSWORD_LENGTH = 512;
  private static final int MAX_NICKNAME_LENGTH = 40;
  private static final int MAX_INTRODUCE_LENGTH = 255;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EntityManager em;

  @Autowired
  private ThumbnailRepository thumbnailRepository;

  @Autowired
  private UserRoleRepository userRoleRepository;

  private User user;
  private final String loginId = getRandomUUIDLengthWith(MAX_LOGIN_ID_LENGTH);
  private final String nickname = getRandomUUIDLengthWith(MAX_NICKNAME_LENGTH);
  private final String password = getRandomUUIDLengthWith(MAX_PASSWORD_LENGTH);
  private final String introduce = getRandomUUIDLengthWith(MAX_INTRODUCE_LENGTH);

  @BeforeEach
  void setUpUser() {
    user = User.builder()
        .loginId(loginId)
        .nickname(nickname)
        .password(password)
        .introduce(introduce)
        .build();
    user = userRepository.save(user);
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
          .loginId(loginId)
          .nickname(getRandomUUIDLengthWith(MAX_NICKNAME_LENGTH))
          .password(getRandomUUIDLengthWith(MAX_PASSWORD_LENGTH))
          .introduce(getRandomUUIDLengthWith(MAX_INTRODUCE_LENGTH))
          .build();
      userRepository.save(duplicateUser);
    }

    private void generateNicknameDuplicateUser() {
      User duplicateUser = User.builder()
          .loginId(getRandomUUIDLengthWith(MAX_LOGIN_ID_LENGTH))
          .nickname(nickname)
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
          .password(password)
          .introduce(getRandomUUIDLengthWith(MAX_INTRODUCE_LENGTH))
          .build();
      userRepository.save(duplicateUser);
    }

    private void generateIntroduceDuplicateUser() {
      User duplicateUser = User.builder()
          .loginId(getRandomUUIDLengthWith(MAX_LOGIN_ID_LENGTH))
          .nickname(getRandomUUIDLengthWith(MAX_NICKNAME_LENGTH))
          .password(getRandomUUIDLengthWith(MAX_PASSWORD_LENGTH))
          .introduce(introduce)
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
    void should_deleteAllUserInfo_when_deleteMySelf() throws IOException {
      User userWithThumbnail = generateUser();

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

    private User generateUser() throws IOException {
      final ThumbnailUtil thumbnailUtil = new ThumbnailFileUtil(thumbnailRepository);
      User userWithThumbnail = User.builder()
          .loginId(getRandomUUIDLengthWith(MAX_LOGIN_ID_LENGTH))
          .nickname(getRandomUUIDLengthWith(MAX_NICKNAME_LENGTH))
          .password(getRandomUUIDLengthWith(MAX_PASSWORD_LENGTH))
          .introduce(getRandomUUIDLengthWith(MAX_INTRODUCE_LENGTH))
          .thumbnail(thumbnailUtil.saveIfExist(getThumbnailFile()).orElseThrow())
          .build();
      userWithThumbnail = userRepository.save(userWithThumbnail);
      em.flush();
      em.clear();
      return userWithThumbnail;
    }

    private MockMultipartFile getThumbnailFile() throws IOException {
      return new MockMultipartFile("thumbnail",
          "testImage_1x1.png", "image/png",
          new FileInputStream("src/test/resources/images/testImage_1x1.png"));
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
    void should_notChangeThumbnail_when_nullThumbnail() throws IOException {
      User beforeUser = generateUser();
      String newIntroduce = "newIntroduce";

      whenUpdateUser(beforeUser, null, newIntroduce);

      checkUserUpdated(beforeUser, beforeUser.getThumbnailURL(), newIntroduce);
      checkThumbnailUpdated(beforeUser);
    }

    @Test
    @DisplayName("소개가 null일 경우 소개는 바뀌어선 안된다.")
    void should_notChangeThumbnail_when_nullIntroduce() throws IOException {
      User beforeUser = generateUser();
      Thumbnail newThumbnail = generateThumbnail();

      whenUpdateUser(beforeUser, newThumbnail, null);

      checkUserUpdated(beforeUser, Optional.of(newThumbnail.getPath()), beforeUser.getIntroduce());
      checkThumbnailUpdated(beforeUser);
    }

    @Test
    @DisplayName("썸네일과 소개가 null일 경우 둘 다 바뀌어선 안된다.")
    void should_notChangeThumbnail_when_nullEverything() throws IOException {
      User beforeUser = generateUser();

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

    private User generateUser() throws IOException {
      Thumbnail thumbnail = generateThumbnail();
      User userWithThumbnail = User.builder()
          .loginId(getRandomUUIDLengthWith(MAX_LOGIN_ID_LENGTH))
          .nickname(getRandomUUIDLengthWith(MAX_NICKNAME_LENGTH))
          .password(getRandomUUIDLengthWith(MAX_PASSWORD_LENGTH))
          .introduce(getRandomUUIDLengthWith(MAX_INTRODUCE_LENGTH))
          .thumbnail(thumbnail)
          .build();
      userWithThumbnail = userRepository.save(userWithThumbnail);
      return userWithThumbnail;
    }

    private Thumbnail generateThumbnail() throws IOException {
      final ThumbnailUtil thumbnailUtil = new ThumbnailFileUtil(thumbnailRepository);
      Thumbnail thumbnail = thumbnailUtil.saveIfExist(getThumbnailFile()).orElseThrow();
      return thumbnail;
    }

    private MockMultipartFile getThumbnailFile() throws IOException {
      return new MockMultipartFile("thumbnail",
          "testImage_1x1.png", "image/png",
          new FileInputStream("src/test/resources/images/testImage_1x1.png"));
    }

    private String getUserThumbnailPath(User userWithThumbnail) {
      return userWithThumbnail.getThumbnailURL()
          .orElseThrow();
    }
  }
}