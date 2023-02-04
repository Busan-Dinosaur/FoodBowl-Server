package com.dinosaur.foodbowl.domain.clip.api;

import com.dinosaur.foodbowl.domain.clip.application.ClipService;
import com.dinosaur.foodbowl.domain.clip.dto.response.ClipStatusResponseDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.util.resolver.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clips")
public class ClipController {

  private final ClipService clipService;

  @PostMapping("/posts/{id}/clip")
  public ResponseEntity<ClipStatusResponseDto> clip(@PathVariable("id") Long postId,
      @LoginUser User user) {
    ClipStatusResponseDto response = clipService.clip(user, postId);

    return ResponseEntity.ok(response);
  }

  @PostMapping("/posts/{id}/unclip")
  public ResponseEntity<ClipStatusResponseDto> unclip(@PathVariable("id") Long postId,
      @LoginUser User user) {
    ClipStatusResponseDto response = clipService.unclip(user, postId);

    return ResponseEntity.ok(response);
  }
}
