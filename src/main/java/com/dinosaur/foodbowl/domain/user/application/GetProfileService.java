package com.dinosaur.foodbowl.domain.user.application;

import com.dinosaur.foodbowl.domain.follow.dao.FollowRepository;
import com.dinosaur.foodbowl.domain.user.dao.UserFindDao;
import com.dinosaur.foodbowl.domain.user.dto.response.ProfileResponseDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetProfileService {

  private final UserFindDao userFindDao;
  private final FollowRepository followRepository;

  public ProfileResponseDto getProfile(long userId) {
    User user = userFindDao.findById(userId);
    long followerCount = followRepository.countByFollowing(user);
    long followingCount = followRepository.countByFollower(user);
    return ProfileResponseDto.of(user, followerCount, followingCount);
  }
}
