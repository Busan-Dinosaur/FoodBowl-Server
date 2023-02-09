package com.dinosaur.foodbowl.domain.comment.application;

import static com.dinosaur.foodbowl.global.error.ErrorCode.COMMENT_NOT_WRITER;

import com.dinosaur.foodbowl.domain.comment.dao.CommentRepository;
import com.dinosaur.foodbowl.domain.comment.dto.request.CommentWriteRequestDto;
import com.dinosaur.foodbowl.domain.comment.dto.response.CommentResponseDto;
import com.dinosaur.foodbowl.domain.comment.entity.Comment;
import com.dinosaur.foodbowl.domain.post.application.PostFindService;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.error.BusinessException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

  private final CommentRepository commentRepository;
  private final CommentFindService commentFindService;
  private final PostFindService postFindService;

  @Transactional
  public void writeComment(User loginUser, CommentWriteRequestDto request) {
    Post post = postFindService.findById(request.getPostId());
    Comment comment = request.toEntity(loginUser, post);

    commentRepository.save(comment);
  }

  @Transactional
  public long updateComment(User user, Long commentId, String message) {
    Comment comment = commentFindService.findById(commentId);

    if (!comment.getUser().equals(user)) {
      throw new BusinessException(user.getId(), "userId", COMMENT_NOT_WRITER);
    }

    comment.updateMessage(message);
    return comment.getPost().getId();
  }

  @Transactional
  public void deleteComment(User user, Long commentId) {
    Comment comment = commentFindService.findById(commentId);

    if (!comment.getUser().equals(user)) {
      throw new BusinessException(user.getId(), "userId", COMMENT_NOT_WRITER);
    }

    commentRepository.delete(comment);
  }

  public List<CommentResponseDto> getComments(final long postId) {
    Post post = postFindService.findById(postId);
    List<Comment> comments = commentRepository.findUnrestrictedComments(post);

    return comments.stream()
        .map(CommentResponseDto::from)
        .collect(Collectors.toList());
  }
}
