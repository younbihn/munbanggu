package com.zerobase.munbanggu.community.controller;

import com.zerobase.munbanggu.community.dto.CommentDto;
import com.zerobase.munbanggu.community.dto.CommentResponseDto;
import com.zerobase.munbanggu.community.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/comments/{communityId}")
    public ResponseEntity<String> addComment(@PathVariable Long communityId, @RequestBody CommentDto commentDto) {
        commentService.addComment(communityId, commentDto);
        return ResponseEntity.ok("댓글이 작성되었습니다.");
    }

    @PostMapping("/comments/{commentId}/replies")
    public ResponseEntity<String> addReply(@PathVariable Long commentId, @RequestBody CommentDto replyDto) {
        commentService.addReply(commentId, replyDto);
        return ResponseEntity.ok("대댓글이 작성되었습니다.");
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<String> updateComment(@PathVariable Long commentId, @RequestBody CommentDto commentDto) {
        commentService.updateComment(commentId, commentDto);
        return ResponseEntity.ok("댓글이 수정되었습니다.");
    }

    @PatchMapping("/comments/replies/{replyId}")
    public ResponseEntity<String> updateReply(@PathVariable Long replyId, @RequestBody CommentDto replyDto) {
        commentService.updateReply(replyId, replyDto);
        return ResponseEntity.ok("대댓글이 수정되었습니다.");
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok("댓글이 삭제되었습니다.");
    }

    @DeleteMapping("/comments/replies/{replyId}")
    public ResponseEntity<String> deleteReply(@PathVariable Long replyId) {
        commentService.deleteReply(replyId);
        return ResponseEntity.ok("대댓글이 삭제되었습니다.");
    }

    @GetMapping("/{communityId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getCommentsAndReplies(@PathVariable Long communityId) {
        List<CommentResponseDto> comments = commentService.getCommentsAndReplies(communityId);
        return ResponseEntity.ok(comments);
    }
}