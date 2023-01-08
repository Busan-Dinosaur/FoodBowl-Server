package com.dinosaur.foodbowl.domain.user.dao;

import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserFindDao {

  private final UserRepository memberRepository;

  public User findById(final Long id) {
    return memberRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
  }
}
