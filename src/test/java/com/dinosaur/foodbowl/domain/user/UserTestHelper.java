package com.dinosaur.foodbowl.domain.user;

import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_LOGIN_ID_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_PASSWORD_LENGTH;

import com.dinosaur.foodbowl.domain.thumbnail.ThumbnailTestHelper;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.dao.UserRepository;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.domain.user.entity.embedded.Nickname;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserTestHelper {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ThumbnailTestHelper thumbnailTestHelper;

    private String getRandomUUIDLengthWith(int length) {
        String randomString = UUID.randomUUID().toString();
        length = Math.min(length, randomString.length());
        return randomString.substring(0, length);
    }

    public UserBuilder builder() {
        return new UserBuilder();
    }

    public final class UserBuilder {

        private Thumbnail thumbnail;
        private String loginId;
        private String password;
        private String nickname;
        private String introduce;

        private UserBuilder() {
        }

        public UserBuilder thumbnail(Thumbnail thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public UserBuilder loginId(String loginId) {
            this.loginId = loginId;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public UserBuilder introduce(String introduce) {
            this.introduce = introduce;
            return this;
        }

        public User build() {
            return userRepository.save(User.builder()
                    .thumbnail(thumbnail)
                    .loginId(loginId != null ?
                            loginId : getRandomUUIDLengthWith(MAX_LOGIN_ID_LENGTH)
                    )
                    .password(password != null ?
                            password : getRandomUUIDLengthWith(MAX_PASSWORD_LENGTH)
                    )
                    .nickname(nickname != null ?
                            Nickname.from(nickname)
                            : Nickname.from(getRandomUUIDLengthWith(Nickname.MAX_NICKNAME_LENGTH))
                    )
                    .introduce(introduce)
                    .build());
        }
    }
}
