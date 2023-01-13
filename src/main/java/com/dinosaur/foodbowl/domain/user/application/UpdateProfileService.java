package com.dinosaur.foodbowl.domain.user.application;

import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.dto.request.UpdateProfileRequestDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.util.auth.AuthUtil;
import com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UpdateProfileService {

  private final AuthUtil authUtil;
  private final ThumbnailUtil thumbnailUtil;

  @Transactional
  public long updateProfile(UpdateProfileRequestDto requestDto) {
    User me = authUtil.getUserByJWT();
    Thumbnail newThumbnail = null;
    try {
      newThumbnail = thumbnailUtil.save(requestDto.getThumbnail());
    } catch (RuntimeException ignore) {
    }
    me.updateProfile(newThumbnail, requestDto.getIntroduce());
    return me.getId();
  }
}
