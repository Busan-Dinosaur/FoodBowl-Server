package com.dinosaur.foodbowl.domain.user.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.dinosaur.foodbowl.domain.thumbnail.dao.ThumbnailRepository;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType;
import com.dinosaur.foodbowl.domain.user.entity.role.UserRole;
import com.dinosaur.foodbowl.global.dao.RepositoryTest;
import com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailFileUtil;
import com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailUtil;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
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
    userRepository.save(user);
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

      UserRole userRole = user.getUserRole();
      assertThat(userRole.getUser()).isEqualTo(user);
      assertThat(userRole.getRole().getId()).isEqualTo(RoleType.USER.getId());
      assertThat(userRole.getRole().getName()).isEqualTo(RoleType.USER.getName());
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
          .thumbnail(thumbnailUtil.save(getThumbnailFile()))
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
}