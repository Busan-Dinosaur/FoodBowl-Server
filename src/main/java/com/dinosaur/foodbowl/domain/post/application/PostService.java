package com.dinosaur.foodbowl.domain.post.application;

import static com.dinosaur.foodbowl.global.error.ErrorCode.POST_HAS_NOT_IMAGE;
import static com.dinosaur.foodbowl.global.error.ErrorCode.POST_NOT_WRITER;

import com.dinosaur.foodbowl.domain.category.dao.CategoryRepository;
import com.dinosaur.foodbowl.domain.category.entity.Category;
import com.dinosaur.foodbowl.domain.photo.application.PhotoService;
import com.dinosaur.foodbowl.domain.photo.dao.PhotoRepository;
import com.dinosaur.foodbowl.domain.photo.entity.Photo;
import com.dinosaur.foodbowl.domain.post.dao.PostRepository;
import com.dinosaur.foodbowl.domain.post.dto.request.PostCreateRequestDto;
import com.dinosaur.foodbowl.domain.post.dto.request.PostUpdateRequestDto;
import com.dinosaur.foodbowl.domain.post.dto.response.PostThumbnailResponseDto;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.store.dao.StoreFindService;
import com.dinosaur.foodbowl.domain.store.entity.Store;
import com.dinosaur.foodbowl.domain.thumbnail.ThumbnailUtil;
import com.dinosaur.foodbowl.domain.thumbnail.dao.ThumbnailRepository;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.application.UserFindService;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.error.BusinessException;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

  private final UserFindService userFindService;
  private final StoreFindService storeFindService;
  private final PostFindService postFindService;
  private final PhotoService photoService;
  private final PostRepository postRepository;
  private final CategoryRepository categoryRepository;
  private final ThumbnailRepository thumbnailRepository;
  private final PhotoRepository photoRepository;
  private final ThumbnailUtil thumbnailUtil;

  @Transactional
  public Long createPost(User user, PostCreateRequestDto request, List<MultipartFile> images) {
    checkImagesEmpty(images);
    Thumbnail thumbnail = thumbnailUtil.saveIfExist(images.get(0))
        .orElseThrow(() -> new BusinessException(images, "images", POST_HAS_NOT_IMAGE));
    Store store = storeFindService.findStoreByName(request.getStore(), request.getAddress());

    Post post = request.toEntity(user, store, thumbnail);
    addCategories(request.getCategoryIds(), post);
    postRepository.save(post);
    photoService.saveAll(images, post);

    return post.getId();
  }

  @Transactional
  public Long updatePost(User user, Long postId, PostUpdateRequestDto request,
      List<MultipartFile> images) {
    Post post = postFindService.findById(postId);
    checkWriter(user, post);
    checkImagesEmpty(images);

    Store store = storeFindService.findStoreByName(request.getStore(), request.getAddress());
    List<Category> categories = getCategories(request.getCategoryIds());

    List<Photo> photos = photoService.saveAll(images, post);
    Thumbnail thumbnail = thumbnailUtil.saveIfExist(images.get(0))
        .orElseThrow(() -> new BusinessException(images, "images", POST_HAS_NOT_IMAGE));

    thumbnailRepository.delete(post.getThumbnail());
    post.update(thumbnail, photos, categories, store, request.getContent());

    return post.getId();
  }

  private List<Category> getCategories(List<Long> categoryIds) {

    return categoryRepository.findAllById(new HashSet<>(categoryIds));
  }

  private void checkImagesEmpty(List<MultipartFile> images) {
    if (images.isEmpty()) {
      throw new BusinessException(images, "images", POST_HAS_NOT_IMAGE);
    }
  }

  public void addCategories(List<Long> categoryIds, Post post) {
    categoryIds.forEach(id -> post.addCategory(categoryRepository.getReferenceById(id)));
  }

  @Transactional
  public void deletePost(User user, Long postId) {
    Post post = postFindService.findById(postId);
    checkWriter(user, post);

    postRepository.delete(post);
  }

  private static void checkWriter(User user, Post post) {
    if (!post.isWriter(user)) {
      throw new BusinessException(post.getId(), "postId", POST_NOT_WRITER);
    }
  }

  public List<PostThumbnailResponseDto> getThumbnails(Long userId, Pageable pageable) {
    User user = userFindService.findById(userId);

    List<Post> posts = postRepository.findThumbnailsByUser(user, pageable);

    return posts.stream()
        .map(PostThumbnailResponseDto::from)
        .collect(Collectors.toList());
  }
}
