package com.dinosaur.foodbowl.domain.user.application;

import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.dao.UserRepository;
import com.dinosaur.foodbowl.domain.user.dto.request.SignUpRequestDto;
import com.dinosaur.foodbowl.domain.user.dto.response.SignUpResponseDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType;
import com.dinosaur.foodbowl.global.config.security.JwtTokenProvider;
import com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailUtil;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<SignUpResponseDto> signUp(SignUpRequestDto request) {
    Thumbnail userThumbnail = saveThumbnailIfExist(request.getThumbnail());
    User user = userRepository.save(request.toEntity(userThumbnail, passwordEncoder));
    String accessToken = jwtTokenProvider.createToken(user.getId(), RoleType.USER);
    SignUpResponseDto signUpResponseDto = SignUpResponseDto.of(user, accessToken);
    return ResponseEntity.created(URI.create("/users/" + user.getId()))
        .body(signUpResponseDto);
  }

  private Thumbnail saveThumbnailIfExist(MultipartFile thumbnail) {
    Thumbnail userThumbnail = null;
    if (thumbnail != null) {
      userThumbnail = thumbnailUtil.save(thumbnail);
    }
    return userThumbnail;
  }
}
