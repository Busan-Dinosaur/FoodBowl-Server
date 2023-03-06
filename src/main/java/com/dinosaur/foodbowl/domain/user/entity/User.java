package com.dinosaur.foodbowl.domain.user.entity;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import com.dinosaur.foodbowl.domain.follow.entity.Follow;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.entity.Role.RoleType;
import com.dinosaur.foodbowl.domain.user.entity.embedded.Nickname;
import com.dinosaur.foodbowl.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class User extends BaseEntity {

    public static final int MAX_LOGIN_ID_LENGTH = 45;
    public static final int MAX_PASSWORD_LENGTH = 512;
    public static final int MAX_INTRODUCE_LENGTH = 255;

    @Getter
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "thumbnail_id")
    private Thumbnail thumbnail;

    @Getter
    @Column(name = "login_id", nullable = false, unique = true, length = MAX_LOGIN_ID_LENGTH)
    private String loginId;

    @Getter
    @Column(name = "password", nullable = false, length = MAX_PASSWORD_LENGTH)
    private String password;

    @Getter
    @Embedded
    private Nickname nickname;

    @Getter
    @Column(name = "introduce", length = MAX_INTRODUCE_LENGTH)
    private String introduce;

    @Getter
    @OneToMany(mappedBy = "user", cascade = ALL, orphanRemoval = true)
    private final Set<UserRole> userRole = new HashSet<>();

    @OneToMany(mappedBy = "follower", cascade = ALL, orphanRemoval = true)
    private final Set<Follow> following = new HashSet<>();

    @OneToMany(mappedBy = "following", cascade = REMOVE)
    private final Set<Follow> follower = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = REMOVE)
    private final List<Post> posts = new ArrayList<>();

    @Builder
    private User(Thumbnail thumbnail, String loginId, String password, Nickname nickname,
            String introduce) {
        this.thumbnail = thumbnail;
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.introduce = introduce;
        this.assignRole(RoleType.ROLE_회원);
    }

    public void assignRole(RoleType roleType) {
        this.userRole.add(UserRole.builder()
                .user(this)
                .role(Role.getRoleBy(roleType))
                .build());
    }

    public Optional<String> getThumbnailURL() {
        return thumbnail == null ? Optional.empty() : Optional.of(thumbnail.getPath());
    }

    public boolean containsRole(RoleType roleType) {
        return userRole.contains(UserRole.builder()
                .user(this)
                .role(Role.getRoleBy(roleType))
                .build());
    }

    public void updateProfile(Thumbnail thumbnail, String introduce) {
        if (thumbnail != null) {
            this.thumbnail = thumbnail;
        }
        if (introduce != null) {
            this.introduce = introduce;
        }
    }

    public void follow(User other) {
        following.add(Follow.builder()
                .follower(this)
                .following(other)
                .build());
    }

    public void unfollow(User other) {
        following.removeIf(follow -> follow.getFollowing().equals(other));
    }

    public boolean isFollowing(User other) {
        return following.stream()
                .anyMatch(follow -> follow.getFollowing().equals(other));
    }

    public long getPostCount() {
        return posts.size();
    }

    public int getFollowerSize() {
        return follower.size();
    }
}
