package com.dinosaur.foodbowl.domain.follow.application;

import com.dinosaur.foodbowl.domain.follow.dao.FollowRepository;
import com.dinosaur.foodbowl.domain.follow.dto.FollowerResponseDto;
import com.dinosaur.foodbowl.domain.follow.dto.FollowingResponseDto;
import com.dinosaur.foodbowl.domain.follow.entity.Follow;
import com.dinosaur.foodbowl.domain.user.dao.UserRepository;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetFollowService {

  private final UserRepository userRepository;
  private final FollowRepository followRepository;

  public List<FollowerResponseDto> getFollowers(User user, Pageable pageable) {
    List<Follow> follows = followRepository.findFollowByFollowing(user, pageable);

    return follows.stream()
        .map(FollowerResponseDto::of)
        .collect(Collectors.toList());
  }

  public List<FollowingResponseDto> getFollowings(User user, Pageable pageable) {
    List<Follow> follows = followRepository.findFollowByFollower(user, pageable);

    return follows.stream()
        .map(FollowingResponseDto::of)
        .collect(Collectors.toList());
  }

}
