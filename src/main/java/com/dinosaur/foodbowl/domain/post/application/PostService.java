package com.dinosaur.foodbowl.domain.post.application;


import com.dinosaur.foodbowl.domain.category.dao.CategoryRepository;
import com.dinosaur.foodbowl.domain.category.entity.Category;
import com.dinosaur.foodbowl.domain.photo.entity.Photo;
import com.dinosaur.foodbowl.domain.post.dao.PostRepository;
import com.dinosaur.foodbowl.domain.post.dto.PostCreateRequestDto;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.store.dao.StoreRepository;
import com.dinosaur.foodbowl.domain.store.dto.StoreDto;
import com.dinosaur.foodbowl.domain.store.entity.Store;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.entity.User;
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
  public Long createPost(User user, PostCreateRequestDto postCreateRequestDto) {
    List<Photo> photos = photoUtil.save(postCreateRequestDto.getPhotoFiles());
    Category category = categoryRepository.getReferenceById(postCreateRequestDto.getCategoryId());
    Store store = getStore(postCreateRequestDto.getStoreDto(), category);
    Thumbnail thumbnail = getThumbnail(postCreateRequestDto);

    Post post = postCreateRequestDto.toEntity(user, store, photos, thumbnail);

    return postRepository.save(post).getId();
  }

  private Thumbnail getThumbnail(PostCreateRequestDto postCreateRequestDto) {
    MultipartFile firstFile = postCreateRequestDto.getPhotoFiles().get(0);
    return thumbnailUtil.saveIfExist(firstFile).orElse(null);
  }

  private Store getStore(StoreDto storeDto, Category category) {
    if (storeRepository.existsByStoreName(storeDto.getStoreName())) {
      return storeRepository.findByStoreName(storeDto.getStoreName());
    }
    return storeDto.toEntity(category);
  }

}
