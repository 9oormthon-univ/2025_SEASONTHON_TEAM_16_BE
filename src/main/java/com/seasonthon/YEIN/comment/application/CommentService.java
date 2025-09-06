package com.seasonthon.YEIN.comment.application;

import com.seasonthon.YEIN.comment.api.dto.request.CommentRequest;
import com.seasonthon.YEIN.comment.api.dto.response.CommentResponse;
import com.seasonthon.YEIN.comment.api.dto.response.MyCommentResponse;
import com.seasonthon.YEIN.comment.domain.Comment;
import com.seasonthon.YEIN.comment.domain.repository.CommentRepository;
import com.seasonthon.YEIN.global.code.status.ErrorStatus;
import com.seasonthon.YEIN.global.exception.GeneralException;
import com.seasonthon.YEIN.post.domain.Post;
import com.seasonthon.YEIN.post.domain.repository.PostRepository;
import com.seasonthon.YEIN.user.domain.User;
import com.seasonthon.YEIN.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createComment(Long postId, CommentRequest request, Long userId) {
        Post post = findPostById(postId);
        User user = findUserById(userId);

        Comment comment = Comment.builder()
                .content(request.content())
                .post(post)
                .user(user)
                .build();

        Comment savedComment = commentRepository.save(comment);
        post.incrementCommentCount();

        return savedComment.getId();
    }

    public Page<CommentResponse> findCommentsByPost(Long postId, Pageable pageable, Long currentUserId) {
        findPostById(postId);

        Page<Comment> comments = commentRepository.findByPostIdOrderByCreatedAt(postId, pageable);

        return comments.map(comment -> CommentResponse.from(comment, comment.isOwner(currentUserId)));
    }

    public Page<MyCommentResponse> findMyComments(Long userId, Pageable pageable) {
        findUserById(userId);

        Page<Comment> comments = commentRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        return comments.map(MyCommentResponse::from);
    }

    @Transactional
    public void updateComment(Long commentId, CommentRequest request, Long userId) {
        Comment comment = findCommentById(commentId);
        validateCommentOwner(comment, userId);

        comment.updateContent(request.content());
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = findCommentById(commentId);
        validateCommentOwner(comment, userId);

        Post post = comment.getPost();
        commentRepository.delete(comment);
        post.decrementCommentCount();
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));
    }

    private void validateCommentOwner(Comment comment, Long userId) {
        if (!comment.isOwner(userId)) {
            throw new GeneralException(ErrorStatus.ACCESS_DENIED);
        }
    }
}
