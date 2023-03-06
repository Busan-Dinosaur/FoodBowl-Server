package com.dinosaur.foodbowl.domain.clip.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ClipStatusResponseDto {

    private String status;

    public static ClipStatusResponseDto from(String status) {
        return ClipStatusResponseDto.builder()
                .status(status)
                .build();
    }
}
