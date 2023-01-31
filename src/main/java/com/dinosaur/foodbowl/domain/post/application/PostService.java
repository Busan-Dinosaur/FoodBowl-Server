package com.dinosaur.foodbowl.domain.post.application;


import static com.dinosaur.foodbowl.global.error.ErrorCode.POST_HAS_NOT_IMAGE;

import com.dinosaur.foodbowl.domain.category.dao.CategoryRepository;
import com.dinosaur.foodbowl.domain.category.entity.Category;
import com.dinosaur.foodbowl.domain.photo.entity.Photo;
import com.dinosaur.foodbowl.domain.post.dao.PostRepository;
import com.dinosaur.foodbowl.domain.post.dto.PostCreateRequestDto;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.store.dao.StoreRepository;
import com.dinosaur.foodbowl.domain.store.entity.Store;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.error.BusinessException;
import com.dinosaur.foodbowl.global.util.photo.PhotoUtil;
import com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final StoreRepository storeRepository;
  private final CategoryRepository categoryRepository;
  private final ThumbnailUtil thumbnailUtil;
  private final PhotoUtil photoUtil;

  @Transactional
  public Long createPost(User user, PostCreateRequestDto postCreateRequestDto, List<MultipartFile> images) {
    checkImages(images);
    List<Photo> photos = photoUtil.save(images);
    Category category = categoryRepository.getReferenceById(postCreateRequestDto.getCategoryId());
    Store store = getStore(postCreateRequestDto, category);
    Thumbnail thumbnail = getThumbnail(images);

    Post post = postCreateRequestDto.toEntity(user, store, photos, thumbnail);

    return postRepository.save(post).getId();
  }

  private void checkImages(List<MultipartFile> images) {
    if (images.isEmpty()) {
      throw new BusinessException(images, "images", POST_HAS_NOT_IMAGE);
    }
  }

  private Thumbnail getThumbnail(List<MultipartFile> images) {
    MultipartFile firstFile = images.get(0);
    return thumbnailUtil.saveIfExist(firstFile).orElse(null);
  }

  private Store getStore(PostCreateRequestDto request, Category category) {
    if (storeRepository.existsByStoreName(request.getStore().getStoreName())) {
      return storeRepository.findByStoreName(request.getStore().getStoreName());
    }
    return request.getStore().toEntity(category, request.getAddress());
  }

}
