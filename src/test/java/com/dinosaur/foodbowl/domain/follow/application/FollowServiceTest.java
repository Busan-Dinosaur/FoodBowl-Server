package com.dinosaur.foodbowl.domain.follow.application;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.user.UserTestHelper.UserBuilder;
import com.dinosaur.foodbowl.domain.user.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FollowServiceTest extends IntegrationTest {

    @Nested
    class 팔로우_성공 {

        @Test
        void 팔로잉을_성공한다() {
            // given
            UserBuilder userBuilder = userTestHelper.builder();
            User me = userBuilder.build();
            User other = userBuilder.build();

            // when
            followService.follow(me, other.getId());
            em.flush();
            em.clear();

            // then
            boolean isFollowed = followRepository.existsByFollowerAndFollowing(me, other);
            Assertions.assertThat(isFollowed).isTrue();
        }

        @Test
        void 팔로잉_되어있다면_팔로우_취소에_성공한다() {
            // given
            UserBuilder userBuilder = userTestHelper.builder();
            User me = userBuilder.build();
            User other = userBuilder.build();
            followService.follow(me, other.getId());
            em.flush();
            em.clear();
            me = userFindService.findById(me.getId());
            other = userFindService.findById(other.getId());

            // when
            followService.unfollow(me, other.getId());
            em.flush();
            em.clear();

            // then
            boolean isFollowed = followRepository.existsByFollowerAndFollowing(me, other);
            Assertions.assertThat(isFollowed).isFalse();
        }
    }
}
