package com.dinosaur.foodbowl.domain.user.application.signup;

import static com.dinosaur.foodbowl.domain.user.exception.UserErrorCode.LOGIN_ID_DUPLICATE;
import static com.dinosaur.foodbowl.domain.user.exception.UserErrorCode.NICKNAME_DUPLICATE;

import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.dao.UserRepository;
import com.dinosaur.foodbowl.domain.user.dto.request.SignUpRequestDto;
import com.dinosaur.foodbowl.domain.user.dto.response.SignUpResponseDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType;
import com.dinosaur.foodbowl.domain.user.exception.signup.LoginIdDuplicateException;
import com.dinosaur.foodbowl.global.config.security.JwtTokenProvider;
import com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignUpService {

  private final ThumbnailUtil thumbnailUtil;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public SignUpResponseDto signUp(SignUpRequestDto request) {
    Thumbnail userThumbnail = saveThumbnailIfExist(request.getThumbnail());
    User user = userRepository.save(request.toEntity(userThumbnail, passwordEncoder));
    String accessToken = jwtTokenProvider.createAccessToken(user.getId(), RoleType.ROLE_회원);
    return SignUpResponseDto.of(user.getId(), user, accessToken);
  }

  private Thumbnail saveThumbnailIfExist(MultipartFile thumbnail) {
    Thumbnail userThumbnail = null;
    if (thumbnail != null) {
      userThumbnail = thumbnailUtil.save(thumbnail);
    }
    return userThumbnail;
  }

  public void checkDuplicateLoginId(String loginId) {
    if (userRepository.existsByLoginId(loginId)) {
      throw new LoginIdDuplicateException(loginId, LOGIN_ID_DUPLICATE);
    }
  }

  public void checkDuplicateNickname(String nickname) {
    if (userRepository.existsByNickname(nickname)) {
      throw new LoginIdDuplicateException(nickname, NICKNAME_DUPLICATE);
    }
  }
}
