package com.dinosaur.foodbowl.domain.clip.application;

import com.dinosaur.foodbowl.domain.clip.dao.ClipRepository;
import com.dinosaur.foodbowl.domain.clip.dto.response.ClipPostThumbnailResponse;
import com.dinosaur.foodbowl.domain.clip.dto.response.ClipStatusResponseDto;
import com.dinosaur.foodbowl.domain.clip.entity.Clip;
import com.dinosaur.foodbowl.domain.post.application.PostFindService;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClipService {

    private final ClipRepository clipRepository;
    private final PostFindService postFindService;

    @Transactional
    public ClipStatusResponseDto clip(User user, Long postId) {
        Post post = postFindService.findById(postId);

        if (!clipRepository.existsClipByUserAndPost(user, post)) {
            clipRepository.save(Clip.builder().user(user).post(post).build());
        }

        return ClipStatusResponseDto.from("ok");
    }

    @Transactional
    public ClipStatusResponseDto unclip(User user, Long postId) {
        Post post = postFindService.findById(postId);
        Optional<Clip> clip = clipRepository.findClipByUserAndPost(user, post);

        if (clip.isPresent()) {
            clipRepository.delete(clip.get());
        }

        return ClipStatusResponseDto.from("ok");
    }

    public List<ClipPostThumbnailResponse> getClipPostThumbnails(final User user, final Pageable pageable) {
        final List<Clip> clips = clipRepository.findClipByUser(user, pageable);

        final List<ClipPostThumbnailResponse> response = new ArrayList<>();

        for (final Clip clip : clips) {
            final Long clipId = clip.getId();
            final String thumbnailPath = clip.getPost().getThumbnail().getPath();
            response.add(new ClipPostThumbnailResponse(clipId, thumbnailPath));
        }

        return response;
    }
}
