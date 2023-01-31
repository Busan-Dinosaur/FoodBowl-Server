package com.dinosaur.foodbowl.domain.auth.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckResponseDto {

  private boolean available;
  private String message;

  @Builder
  private CheckResponseDto(boolean available, String message) {
    this.available = available;
    this.message = message;
  }

  public static CheckResponseDto of(boolean available, String message) {
    return CheckResponseDto.builder()
        .available(available)
        .message(message)
        .build();
  }
}
