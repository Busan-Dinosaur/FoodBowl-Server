package com.dinosaur.foodbowl.domain.post.entity;

import static jakarta.persistence.CascadeType.REMOVE;

import com.dinosaur.foodbowl.domain.category.entity.Category;
import com.dinosaur.foodbowl.domain.category.entity.Category.CategoryType;
import com.dinosaur.foodbowl.domain.clip.entity.Clip;
import com.dinosaur.foodbowl.domain.comment.entity.Comment;
import com.dinosaur.foodbowl.domain.photo.entity.Photo;
import com.dinosaur.foodbowl.domain.store.entity.Store;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class Post extends BaseEntity {

  @Getter
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, updatable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "thumbnail_id", nullable = false)
  private Thumbnail thumbnail;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id", nullable = false)
  private Store store;

  @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
  private String content;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "post", cascade = REMOVE)
  private final List<Photo> photos = new ArrayList<>();

  @OneToMany(mappedBy = "post", cascade = REMOVE)
  private final List<PostCategory> categories = new ArrayList<>();

  @OneToMany(mappedBy = "post", cascade = REMOVE)
  private final List<Clip> clips = new ArrayList<>();

  @OneToMany(mappedBy = "post", cascade = REMOVE)
  private final List<Comment> comments = new ArrayList<>();

  @Builder
  private Post(User user, Thumbnail thumbnail, Store store, String content) {
    this.user = user;
    this.thumbnail = thumbnail;
    this.store = store;
    this.content = content;
  }

  public List<String> getPhotoPaths() {
    return photos.stream()
        .map(Photo::getPath)
        .collect(Collectors.toList());
  }

  public List<String> getCategoryNames() {
    return categories.stream()
        .map(PostCategory::getCategory)
        .map(Category::getCategoryType)
        .map(CategoryType::toString)
        .collect(Collectors.toList());
  }

  public int getClipSize() {
    return clips.size();
  }

  public int getCommentSize() {
    return comments.size();
  }

  public boolean isCliped(final User user) {
    return clips.stream()
        .filter(clip -> clip.getUser().equals(user))
        .findAny()
        .isPresent();
  }
}
