package com.dinosaur.foodbowl.domain.blame;

import com.dinosaur.foodbowl.domain.blame.dao.BlameRepository;
import com.dinosaur.foodbowl.domain.blame.entity.Blame;
import com.dinosaur.foodbowl.domain.blame.entity.Blame.TargetType;
import com.dinosaur.foodbowl.domain.user.UserTestHelper;
import com.dinosaur.foodbowl.domain.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BlameTestHelper {

    @Autowired
    private BlameRepository blameRepository;

    @Autowired
    private UserTestHelper userTestHelper;

    public BlameBuilder builder() {
        return new BlameBuilder();
    }

    public final class BlameBuilder {

        private User user;
        private Long targetId;
        private TargetType targetType;

        public BlameBuilder user(User user) {
            this.user = user;
            return this;
        }

        public BlameBuilder targetId(Long targetId) {
            this.targetId = targetId;
            return this;
        }

        public BlameBuilder targetType(TargetType targetType) {
            this.targetType = targetType;
            return this;
        }

        public Blame build() {
            return blameRepository.save(Blame.builder()
                    .user(user != null ? user : userTestHelper.builder().build())
                    .targetId(targetId != null ? targetId : 1L)
                    .targetType(targetType != null ? targetType : TargetType.USER)
                    .build());
        }
    }
}
