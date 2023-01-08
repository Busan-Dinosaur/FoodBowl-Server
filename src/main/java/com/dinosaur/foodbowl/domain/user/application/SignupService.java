package com.dinosaur.foodbowl.domain.user.application;

import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.dao.UserRepository;
import com.dinosaur.foodbowl.domain.user.dto.request.SignupRequestDto;
import com.dinosaur.foodbowl.domain.user.dto.response.SignupResponseDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.config.security.JwtTokenProvider;
import com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignupService {

  private final ThumbnailUtil thumbnailUtil;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public SignupResponseDto signup(SignupRequestDto request) {
    Thumbnail userThumbnail = null;
    if (request.getThumbnail() != null) {
      userThumbnail = thumbnailUtil.save(request.getThumbnail());
    }
    User user = userRepository.save(request.toEntity(userThumbnail, passwordEncoder));
    String accessToken = jwtTokenProvider.createToken(user.getId(), "ROLE_USER");
    return SignupResponseDto.of(user, accessToken);
  }
}