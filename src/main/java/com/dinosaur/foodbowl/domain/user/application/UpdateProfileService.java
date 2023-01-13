package com.dinosaur.foodbowl.domain.user.application;

import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.dto.request.UpdateProfileRequestDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UpdateProfileService {

  private final ThumbnailUtil thumbnailUtil;

  @Transactional
  public long updateProfile(User me, UpdateProfileRequestDto requestDto) {
    Optional<Thumbnail> newThumbnail = saveThumbnailIfExist(requestDto.getThumbnail());
    me.updateProfile(newThumbnail.orElse(null), requestDto.getIntroduce());
    return me.getId();
  }

  private Optional<Thumbnail> saveThumbnailIfExist(MultipartFile thumbnail) {
    if (thumbnail == null) {
      return Optional.empty();
    }
    return Optional.of(thumbnailUtil.save(thumbnail));
  }
}
