package com.zerobase.munbanggu.studyboard.controller;

import static com.zerobase.munbanggu.common.type.ErrorCode.INVALID_TOKEN;

import com.zerobase.munbanggu.auth.TokenProvider;
import com.zerobase.munbanggu.common.dto.PageResponse;
import com.zerobase.munbanggu.common.exception.InvalidTokenException;
import com.zerobase.munbanggu.studyboard.model.dto.CommentRequest;
import com.zerobase.munbanggu.studyboard.model.dto.CommentResponse;
import com.zerobase.munbanggu.studyboard.service.StudyCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/post-comment")
@RequiredArgsConstructor
public class StudyCommentController {

    private final StudyCommentService studyCommentService;
    private final TokenProvider tokenProvider;

    private static final String AUTHORIZATION_HEADER = "Authorization";


    @PostMapping("/{post_id}")
    public ResponseEntity<String> create(@PathVariable("post_id") Long postId, @RequestBody CommentRequest commentRequest,
            @RequestHeader(value = AUTHORIZATION_HEADER) String authHeader) {
        String token = tokenProvider.getRawToken(authHeader);
        studyCommentService.create(postId, commentRequest, token);
        return ResponseEntity.ok().body("댓글이 작성되었습니다.");
    }

    @DeleteMapping("/{post_id}/{comment_id}")
    public ResponseEntity<String> delete(@PathVariable("post_id") Long postId, @PathVariable("comment_id") Long commentId,
            @RequestHeader(value = AUTHORIZATION_HEADER) String authHeader) {
        if (!StringUtils.hasText(authHeader)) {
            throw new InvalidTokenException(INVALID_TOKEN);
        }
        String token = tokenProvider.getRawToken(authHeader);
        studyCommentService.delete(postId, commentId, token);
        return ResponseEntity.ok().body("댓글이 삭제되었습니다.");
    }

    @GetMapping("/{post_id}")
    public ResponseEntity<PageResponse<CommentResponse>> retrieveAllComments(@PathVariable("post_id") Long postId,
            @RequestHeader(value = AUTHORIZATION_HEADER) String authHeader, @PageableDefault() Pageable pageable) {
        String token = tokenProvider.getRawToken(authHeader);

        return ResponseEntity.ok().body(PageResponse.from(
                studyCommentService.retrieveAllComments(postId, token, pageable)));
    }
}
