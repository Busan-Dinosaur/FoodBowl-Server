package com.dinosaur.foodbowl.domain.clip.api;

import com.dinosaur.foodbowl.domain.clip.application.ClipService;
import com.dinosaur.foodbowl.domain.clip.dto.response.ClipPostThumbnailResponse;
import com.dinosaur.foodbowl.domain.clip.dto.response.ClipStatusResponseDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.util.resolver.LoginUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clips")
public class ClipController {

    private final ClipService clipService;

    @PostMapping("/posts/{id}/clip")
    public ResponseEntity<ClipStatusResponseDto> clip(
            @PathVariable("id") Long postId,
            @LoginUser User user
    ) {
        ClipStatusResponseDto response = clipService.clip(user, postId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/posts/{id}/unclip")
    public ResponseEntity<ClipStatusResponseDto> unclip(
            @PathVariable("id") Long postId,
            @LoginUser User user
    ) {
        ClipStatusResponseDto response = clipService.unclip(user, postId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/thumbnails")
    public ResponseEntity<List<ClipPostThumbnailResponse>> getClipPostThumbnails(
            @LoginUser User user,
            @PageableDefault(size = 18, sort = "createdAt", direction = Direction.DESC) Pageable pageable
    ) {
        final List<ClipPostThumbnailResponse> response = clipService.getClipPostThumbnails(user, pageable);

        return ResponseEntity.ok(response);
    }
}
