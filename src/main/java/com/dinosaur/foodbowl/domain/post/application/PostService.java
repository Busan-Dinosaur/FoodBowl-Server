package com.dinosaur.foodbowl.domain.post.application;


import static com.dinosaur.foodbowl.global.error.ErrorCode.POST_HAS_NOT_IMAGE;
import static com.dinosaur.foodbowl.global.error.ErrorCode.POST_NOT_WRITER;

import com.dinosaur.foodbowl.domain.category.dao.CategoryRepository;
import com.dinosaur.foodbowl.domain.photo.entity.Photo;
import com.dinosaur.foodbowl.domain.post.dao.PostRepository;
import com.dinosaur.foodbowl.domain.post.dto.request.PostCreateRequestDto;
import com.dinosaur.foodbowl.domain.post.dto.request.PostUpdateRequestDto;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.store.dao.StoreFindService;
import com.dinosaur.foodbowl.domain.store.entity.Store;
import com.dinosaur.foodbowl.domain.thumbnail.ThumbnailUtil;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.error.BusinessException;
import com.dinosaur.foodbowl.global.util.photo.PhotoUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

  private final StoreFindService storeFindService;
  private final PostFindService postFindService;
  private final PostRepository postRepository;
  private final CategoryRepository categoryRepository;
  private final ThumbnailUtil thumbnailUtil;
  private final PhotoUtil photoUtil;

  @Transactional
  public Long createPost(User user, PostCreateRequestDto request, List<MultipartFile> images) {
    checkImages(images);
    List<Photo> photos = photoUtil.save(images);
    Thumbnail thumbnail = thumbnailUtil.saveIfExist(images.get(0)).orElse(null);
    Store store = storeFindService.findStoreByName(request.getStore(), request.getAddress());

    Post post = request.toEntity(user, store, photos, thumbnail);
    addCategories(request.getCategoryIds(), post);

    return postRepository.save(post).getId();
  }

  @Transactional
  public Long updatePost(User user, Long postId, PostUpdateRequestDto request,
      List<MultipartFile> images) {
    Post post = postFindService.findById(postId);
    checkWriter(user, post);

    checkImages(images);
    List<Photo> photos = photoUtil.save(images);
    Thumbnail thumbnail = thumbnailUtil.saveIfExist(images.get(0)).orElse(null);
    Store store = storeFindService.findStoreByName(request.getStore(), request.getAddress());

    post.update(thumbnail, store, request.getContent(), photos);
    addCategories(request.getCategoryIds(), post);

    return post.getId();
  }

  private void checkImages(List<MultipartFile> images) {
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
}
