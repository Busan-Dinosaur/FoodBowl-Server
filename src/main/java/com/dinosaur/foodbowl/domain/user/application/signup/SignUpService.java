package com.dinosaur.foodbowl.domain.user.application.signup;

import static com.dinosaur.foodbowl.global.error.ErrorCode.LOGIN_ID_DUPLICATE;
import static com.dinosaur.foodbowl.global.error.ErrorCode.NICKNAME_DUPLICATE;

import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.dao.UserRepository;
import com.dinosaur.foodbowl.domain.user.dto.request.SignUpRequestDto;
import com.dinosaur.foodbowl.domain.user.dto.response.SignUpResponseDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.error.BusinessException;
import com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignUpService {

  private final ThumbnailUtil thumbnailUtil;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public SignUpResponseDto signUp(SignUpRequestDto request) {
    checkDuplicateLoginId(request.getLoginId());
    checkDuplicateNickname(request.getNickname());

    Optional<Thumbnail> userThumbnail = thumbnailUtil.saveIfExist(request.getThumbnail());
    User user = userRepository.save(request.toEntity(userThumbnail.orElse(null), passwordEncoder));
    return SignUpResponseDto.of(user);
  }

  private void checkDuplicateLoginId(String loginId) {
    if (userRepository.existsByLoginId(loginId)) {
      throw new BusinessException(loginId, "loginId", LOGIN_ID_DUPLICATE);
    }
  }

  private void checkDuplicateNickname(String nickname) {
    if (userRepository.existsByNickname(nickname)) {
      throw new BusinessException(nickname, "nickname", NICKNAME_DUPLICATE);
    }
  }
}
