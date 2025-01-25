package com.zerobase.munbanggu.community.service;

import com.zerobase.munbanggu.community.dto.CommentDto;
import com.zerobase.munbanggu.community.dto.CommentResponseDto;

import java.util.List;

public interface CommentService {
    void addComment(Long communityId, CommentDto commentDto);
    void addReply(Long commentId, CommentDto replyDto);
    void updateComment(Long commentId, CommentDto commentDto);
    void updateReply(Long replyId, CommentDto replyDto);
    void deleteComment(Long commentId);
    void deleteReply(Long replyId);
    List<CommentResponseDto> getCommentsAndReplies(Long communityId);
}