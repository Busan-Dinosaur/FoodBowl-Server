package com.dinosaur.foodbowl.domain.user.application;

import com.dinosaur.foodbowl.domain.thumbnail.ThumbnailUtil;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.dto.request.UpdateProfileRequestDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UpdateProfileService {

    private final ThumbnailUtil thumbnailUtil;

    @Transactional
    public long updateProfile(User me, UpdateProfileRequestDto requestDto) {
        Optional<Thumbnail> newThumbnail = thumbnailUtil.saveIfExist(requestDto.getThumbnail());
        me.updateProfile(newThumbnail.orElse(null), requestDto.getIntroduce());
        return me.getId();
    }
}
