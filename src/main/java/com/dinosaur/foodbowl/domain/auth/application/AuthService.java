package com.dinosaur.foodbowl.domain.auth.application;

import static com.dinosaur.foodbowl.global.error.ErrorCode.LOGIN_ID_DUPLICATE;
import static com.dinosaur.foodbowl.global.error.ErrorCode.NICKNAME_DUPLICATE;
import static com.dinosaur.foodbowl.global.error.ErrorCode.PASSWORD_NOT_MATCH;

import com.dinosaur.foodbowl.domain.auth.dto.request.LoginRequestDto;
import com.dinosaur.foodbowl.domain.auth.dto.request.SignUpRequestDto;
import com.dinosaur.foodbowl.domain.auth.dto.response.SignUpResponseDto;
import com.dinosaur.foodbowl.domain.thumbnail.ThumbnailUtil;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.dao.UserFindDao;
import com.dinosaur.foodbowl.domain.user.dao.UserRepository;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.domain.user.entity.embedded.Nickname;
import com.dinosaur.foodbowl.global.error.BusinessException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

  private final UserFindDao userFindDao;
  private final UserRepository userRepository;
  private final ThumbnailUtil thumbnailUtil;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public SignUpResponseDto signUp(SignUpRequestDto request) {
    checkDuplicateLoginId(request.getLoginId());
    checkDuplicateNickname(request.getNickname().getNickname());

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
    if (userRepository.existsByNickname(Nickname.from(nickname))) {
      throw new BusinessException(nickname, "nickname", NICKNAME_DUPLICATE);
    }
  }

  public long login(LoginRequestDto loginRequestDto) {
    User user = userFindDao.findByLoginId(loginRequestDto.getLoginId());

    if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
      throw new BusinessException(loginRequestDto.getPassword(), "password", PASSWORD_NOT_MATCH);
    }

    return user.getId();
  }
}
