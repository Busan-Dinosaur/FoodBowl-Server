package com.dinosaur.foodbowl.domain.user.application;

import com.dinosaur.foodbowl.domain.user.dao.UserRepository;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.util.auth.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeleteAccountService {

  private final AuthUtil authUtil;
  private final UserRepository userRepository;

  @Transactional
  public void deleteMySelf() {
    User me = authUtil.getUserByJWT();
    userRepository.delete(me);
  }
}
