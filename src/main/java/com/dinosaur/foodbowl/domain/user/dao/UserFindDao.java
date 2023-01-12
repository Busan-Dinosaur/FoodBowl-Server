package com.dinosaur.foodbowl.domain.user.dao;

import static com.dinosaur.foodbowl.domain.user.exception.UserErrorCode.USER_NOT_FOUND;

import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.domain.user.exception.UserException;
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
        .orElseThrow(() -> new UserException(id, "userId", USER_NOT_FOUND));
  }
}
