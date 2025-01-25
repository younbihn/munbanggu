package com.zerobase.munbanggu.studyboard.service;

import com.zerobase.munbanggu.auth.TokenProvider;
import com.zerobase.munbanggu.common.exception.NoPermissionException;
import com.zerobase.munbanggu.common.exception.NotFoundPostException;
import com.zerobase.munbanggu.common.exception.NotFoundUserException;
import com.zerobase.munbanggu.common.type.ErrorCode;
import com.zerobase.munbanggu.common.util.StudyUtil;
import com.zerobase.munbanggu.studyboard.model.dto.CommentRequest;
import com.zerobase.munbanggu.studyboard.model.dto.CommentResponse;
import com.zerobase.munbanggu.studyboard.model.entity.StudyBoardPost;
import com.zerobase.munbanggu.studyboard.model.entity.StudyComment;
import com.zerobase.munbanggu.studyboard.repository.StudyBoardPostRepository;
import com.zerobase.munbanggu.studyboard.repository.StudyCommentRepository;
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
public class StudyCommentService {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final StudyBoardPostRepository studyBoardPostRepository;
    private final StudyCommentRepository studyCommentRepository;
    private final StudyUtil studyUtil;

    @Transactional
    public void create(Long postId, CommentRequest commentRequest, String token) {
        User user = findUser(tokenProvider.getId(token));
        StudyBoardPost post = findPost(postId);

        Long studyId = post.getStudy().getId();

        if (!studyUtil.existStudyMember(studyId, user.getId())) {
            throw new NoPermissionException(ErrorCode.NOT_FOUND_STUDY_MEMBER);
        }

        StudyComment comment = StudyComment.builder()
                .content(commentRequest.getContent())
                .user(user)
                .studyBoardPost(post)
                .build();

        StudyComment parentComment;
        if (commentRequest.getParentId() != null) {
            parentComment = studyCommentRepository.findById(commentRequest.getParentId())
                    .orElseThrow(() -> new NotFoundPostException(ErrorCode.NOT_FOUND_COMMENT));

            comment.updateParent(parentComment);
        }
        studyCommentRepository.save(comment);
    }

    @Transactional
    public void delete(Long postId, Long commentId, String token) {
        StudyBoardPost post = findPost(postId);
        StudyComment comment = findComment(commentId);
        StudyComment deletableComment = getDeletableComment(comment);
        Long userId = tokenProvider.getId(token);
        Long studyId = post.getStudy().getId();

        if (!studyUtil.existStudyMember(studyId, userId)) {
            throw new NoPermissionException(ErrorCode.NOT_FOUND_STUDY_MEMBER);
        }

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
                studyCommentRepository.delete(deletableComment);
            }
        }
    }

    public Page<CommentResponse> retrieveAllComments(Long postId, String token, Pageable pageable) {
        Long userId = tokenProvider.getId(token);
        StudyBoardPost post = findPost(postId);
        Long studyId = post.getStudy().getId();
        if (!studyUtil.existStudyMember(studyId, userId)) {
            throw new NoPermissionException(ErrorCode.NOT_FOUND_STUDY_MEMBER);
        }
        Page<StudyComment> commentPage = studyCommentRepository.findByStudyBoardPostId(postId, pageable);
        return commentPage.map(CommentResponse::from);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(ErrorCode.NOT_FOUND_USER_ID));
    }

    private StudyBoardPost findPost(Long postId) {
        return studyBoardPostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundPostException(ErrorCode.NOT_FOUND_POST));
    }

    private StudyComment findComment(Long commentId) {
        return studyCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundPostException(ErrorCode.NOT_FOUND_COMMENT));
    }

    private StudyComment getDeletableComment(StudyComment comment) {
        StudyComment parent = comment.getParent();

        if (parent == null) {
            List<StudyComment> nonDeletedChildren = comment.getChildren().stream()
                    .filter(c -> !c.isDeleted()).collect(Collectors.toList());

            if (!nonDeletedChildren.isEmpty()) {
                comment.setDeleted(true);
                return null;
            }
        } else if (parent.isDeleted() && parent.getChildren().size() > 1) {
            List<StudyComment> nonDeletedChildren = parent.getChildren().stream().filter(c -> !c.isDeleted()).collect(
                    Collectors.toList());

            if (nonDeletedChildren.size() > 1) {
                comment.setDeleted(true);
                return null;
            }
            studyCommentRepository.delete(parent);
        } else if (parent.isDeleted() && parent.getChildren().size() == 1) {
            studyCommentRepository.delete(parent);
        }
        return comment;
    }
}
