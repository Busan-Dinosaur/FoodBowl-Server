package com.dinosaur.foodbowl.domain.auth.dto.response;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckResponseDto {

  private boolean available;
  private String message;
}
