package com.dinosaur.foodbowl.domain.post.application;


import static com.dinosaur.foodbowl.global.error.ErrorCode.POST_HAS_NOT_IMAGE;

import com.dinosaur.foodbowl.domain.category.dao.CategoryRepository;
import com.dinosaur.foodbowl.domain.category.entity.Category;
import com.dinosaur.foodbowl.domain.photo.entity.Photo;
import com.dinosaur.foodbowl.domain.post.dao.PostRepository;
import com.dinosaur.foodbowl.domain.post.dto.PostCreateRequestDto;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.store.dao.StoreFindDao;
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

  private final PostRepository postRepository;
  private final StoreFindDao storeFindDao;
  private final CategoryRepository categoryRepository;
  private final ThumbnailUtil thumbnailUtil;
  private final PhotoUtil photoUtil;

  @Transactional
  public Long createPost(User user, PostCreateRequestDto request, List<MultipartFile> images) {
    checkImages(images);
    List<Photo> photos = photoUtil.save(images);
    Category category = categoryRepository.getReferenceById(request.getCategoryId());
    Store store = storeFindDao.findStoreByName(request.getStore(), request.getAddress(), category);
    Thumbnail thumbnail = thumbnailUtil.saveIfExist(images.get(0)).orElse(null);

    Post post = request.toEntity(user, store, photos, thumbnail);

    return postRepository.save(post).getId();
  }

  private void checkImages(List<MultipartFile> images) {
    if (images.isEmpty()) {
      throw new BusinessException(images, "images", POST_HAS_NOT_IMAGE);
    }
  }

}
