package com.dinosaur.foodbowl.domain.post.entity;

import static jakarta.persistence.CascadeType.ALL;

import com.dinosaur.foodbowl.domain.category.entity.Category;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class Post extends BaseEntity {

  @Id
  @Getter
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, updatable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "thumbnail_id", nullable = false)
  private Thumbnail thumbnail;

  @ManyToOne(fetch = FetchType.LAZY, cascade = ALL)
  @JoinColumn(name = "store_id", nullable = false)
  private Store store;

  @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
  private String content;

  @OneToMany(mappedBy = "post", cascade = ALL)
  private List<Photo> photos = new ArrayList<>();

  @OneToMany(mappedBy = "post")
  private Set<PostCategory> postCategories = new HashSet<>();

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Builder
  private Post(User user, Thumbnail thumbnail, Store store, String content, List<Photo> photos,
      Set<PostCategory> postCategories) {
    this.user = user;
    this.thumbnail = thumbnail;
    this.store = store;
    this.content = content;
    this.photos = photos;
    this.postCategories = postCategories;
  }

  public void addCategory(Category category) {
    this.postCategories.add(PostCategory.builder()
        .post(this)
        .category(category)
        .build());
  }
}
