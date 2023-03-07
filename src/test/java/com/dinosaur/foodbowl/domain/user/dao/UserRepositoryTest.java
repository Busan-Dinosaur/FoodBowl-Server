package com.dinosaur.foodbowl.domain.user.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.UserTestHelper.UserBuilder;
import com.dinosaur.foodbowl.domain.user.entity.Role.RoleType;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.domain.user.entity.embedded.Nickname;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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
    class 유니크_컬럼 {

        @Test
        void 중복이_발생하면_예외가_발생한다() {
            assertThatThrownBy(() -> userBuilder.loginId(user.getLoginId()).build());
            assertThatThrownBy(() -> userBuilder.nickname((user.getNickname()).getNickname()).build());
        }

        @Test
        void 중복이_허용되어_있으면_예외가_발생하지_않는다() {
            assertThatNoException().isThrownBy(() -> userBuilder.password(user.getPassword()).build());
            assertThatNoException().isThrownBy(() -> userBuilder.introduce(user.getIntroduce()).build());
        }
    }

    @Nested
    class 존재 {

        @Test
        void 로그인_아이디가_존재하면_true_반환한다() {
            String loginId = user.getLoginId();

            boolean result = userRepository.existsByLoginId(loginId);

            assertThat(result).isTrue();
        }

        @Test
        void 로그인_아이디가_존재하지_않으면_false_반환한다() {
            String loginId = "not-exist-loginId";

            boolean result = userRepository.existsByLoginId(loginId);

            assertThat(result).isFalse();
        }

        @Test
        void 닉네임이_존재하면_true_반환한다() {
            Nickname nickname = Nickname.from(user.getNickname().getNickname());

            boolean result = userRepository.existsByNickname(nickname);

            assertThat(result).isTrue();
        }

        @Test
        void 닉네임이_존재하지_않으면_false_반환한다() {
            Nickname nickname = Nickname.from("not-exist-nickname");

            boolean result = userRepository.existsByNickname(nickname);

            assertThat(result).isFalse();
        }
    }

    @Nested
    class 권한 {

        @Test
        void 회원_저장_시_권한을_가지고_있는다() {
            em.flush();
            em.clear();

            user = userRepository.findById(user.getId()).orElseThrow();

            assertThat(user.containsRole(RoleType.ROLE_회원)).isTrue();
        }

        @Test
        void 중복되는_권한을_가지지_않는다() {
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
    class 회원_삭제 {

        @Test
        void 회원을_삭제하면_회원과_관련된_모든_정보가_삭제된다() {
            User userWithThumbnail = userBuilder.thumbnail(thumbnailTestHelper.generateThumbnail()).build();

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
            return userWithThumbnail.getThumbnailURL().orElseThrow();
        }
    }

    @Nested
    class 프로필_업데이트 {

        @Test
        void 썸네일이_null_이라면_썸네일은_바뀌지_않는다() {
            User beforeUser = userBuilder
                    .thumbnail(thumbnailTestHelper.generateThumbnail())
                    .build();
            String newIntroduce = "newIntroduce";

            whenUpdateUser(beforeUser, null, newIntroduce);

            checkUserUpdated(beforeUser, beforeUser.getThumbnailURL(), newIntroduce);
            checkThumbnailUpdated(beforeUser);
        }

        @Test
        void 소개가_null_이라면_소개는_바뀌지_않는다() {
            User beforeUser = userBuilder
                    .thumbnail(thumbnailTestHelper.generateThumbnail())
                    .build();
            Thumbnail newThumbnail = thumbnailTestHelper.generateThumbnail();

            whenUpdateUser(beforeUser, newThumbnail, null);

            checkUserUpdated(beforeUser, Optional.of(newThumbnail.getPath()), beforeUser.getIntroduce());
            checkThumbnailUpdated(beforeUser);
        }

        @Test
        void 썸네일_소개_null_이라면_바뀌지_않는다() {
            User beforeUser = userBuilder
                    .thumbnail(thumbnailTestHelper.generateThumbnail())
                    .build();

            whenUpdateUser(beforeUser, null, null);

            checkUserUpdated(beforeUser, beforeUser.getThumbnailURL(), beforeUser.getIntroduce());
            checkThumbnailUpdated(beforeUser);
        }

        private void checkUserUpdated(User beforeUser, Optional<String> newThumbnailPath, String newIntroduce) {
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
            return userWithThumbnail.getThumbnailURL().orElseThrow();
        }
    }

    @Nested
    class 팔로잉 {

        @Test
        void 팔로우는_한번만_가능하다() {
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
