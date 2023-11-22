package com.zerobase.munbanggu.studyboard.service;

import com.zerobase.munbanggu.auth.TokenProvider;
import com.zerobase.munbanggu.studyboard.exception.NoPermissionException;
import com.zerobase.munbanggu.studyboard.exception.NotFoundPostException;
import com.zerobase.munbanggu.studyboard.model.dto.CommentRequest;
import com.zerobase.munbanggu.studyboard.model.dto.CommentResponse;
import com.zerobase.munbanggu.studyboard.model.entity.Comment;
import com.zerobase.munbanggu.studyboard.model.entity.StudyBoardPost;
import com.zerobase.munbanggu.studyboard.repository.CommentRepository;
import com.zerobase.munbanggu.studyboard.repository.StudyBoardPostRepository;
import com.zerobase.munbanggu.type.ErrorCode;
import com.zerobase.munbanggu.user.exception.NotFoundUserException;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final StudyBoardPostRepository studyBoardPostRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void create(Long postId, CommentRequest commentRequest, String token) {
        User user = findUser(tokenProvider.getId(token));
        StudyBoardPost post = findPost(postId);

        Comment comment = Comment.builder()
                .content(commentRequest.getContent())
                .user(user)
                .studyBoardPost(post)
                .build();

        Comment parentComment;
        if (commentRequest.getParentId() != null) {
            parentComment = commentRepository.findById(commentRequest.getParentId())
                    .orElseThrow(() -> new NotFoundPostException(ErrorCode.NOT_FOUND_COMMENT));

            comment.updateParent(parentComment);
        }
        commentRepository.save(comment);
    }

    @Transactional
    public void delete(Long postId, Long commentId, String token) {
        StudyBoardPost post = findPost(postId);
        Comment comment = findComment(commentId);
        Comment deletableComment = getDeletableComment(comment);
        Long userId = tokenProvider.getId(token);

        if (!comment.getStudyBoardPost().getId().equals(post.getId())) {
            throw new NotFoundPostException(ErrorCode.NOT_FOUND_POST);
        }

        if (!comment.getUser().getId().equals(userId)) {
            throw new NoPermissionException(ErrorCode.NO_PERMISSION_TO_MODIFY);
        }

        if (deletableComment != null) {
            if (!deletableComment.getChildren().isEmpty()) {
                deletableComment.setDeleted(true);
            } else {
                commentRepository.delete(deletableComment);
            }
        }
    }

    public Page<CommentResponse> retrieveAllComments(Long postId, String token, Pageable pageable) {
        // TODO: 스터디 가입되었는지 조회
        Long userId = tokenProvider.getId(token);
        StudyBoardPost post = findPost(postId);
        Page<CommentResponse> commentResponses = null;
        if (post != null) {
            Page<Comment> commentPage = commentRepository.findByStudyBoardPostId(postId, pageable);
            commentResponses = commentPage.map(CommentResponse::from);
        }
        return commentResponses;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(ErrorCode.NOT_FOUND_USER_ID));
    }

    private StudyBoardPost findPost(Long postId) {
        return studyBoardPostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundPostException(ErrorCode.NOT_FOUND_POST));
    }

    private Comment findComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundPostException(ErrorCode.NOT_FOUND_COMMENT));
    }

    private Comment getDeletableComment(Comment comment) {
        Comment parent = comment.getParent();

        if (parent == null) {
            List<Comment> nonDeletedChildren = comment.getChildren().stream()
                    .filter(c -> !c.isDeleted()).collect(Collectors.toList());

            if (!nonDeletedChildren.isEmpty()) {
                comment.setDeleted(true);
                return null;
            }
        } else if (parent.isDeleted() && parent.getChildren().size() > 1) {
            // 삭제 안된 자식 댓글 리스트
            List<Comment> nonDeletedChildren = parent.getChildren().stream().filter(c -> !c.isDeleted()).collect(
                    Collectors.toList());
            // 삭제 안된 자식 댓글들이 있다면!
            if (nonDeletedChildren.size() > 1) {
                comment.setDeleted(true);
                return null;
            }
            commentRepository.delete(parent);
        } else if (parent.isDeleted() && parent.getChildren().size() == 1) {
            commentRepository.delete(parent);
        }
        return comment;
    }
}
