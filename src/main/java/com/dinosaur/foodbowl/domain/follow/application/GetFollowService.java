package com.dinosaur.foodbowl.domain.follow.application;

import com.dinosaur.foodbowl.domain.follow.dto.FollowGetResponseDto;
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

  public List<FollowGetResponseDto> getFollowers(User user, Pageable pageable) {

    return userRepository.findFollowersByUser(user, pageable).stream()
        .map(FollowGetResponseDto::toDto)
        .collect(Collectors.toList());
  }

  public List<FollowGetResponseDto> getFollowings(User user, Pageable pageable) {

    return userRepository.findFollowingsByUser(user, pageable).stream()
        .map(FollowGetResponseDto::toDto)
        .collect(Collectors.toList());
  }

}
