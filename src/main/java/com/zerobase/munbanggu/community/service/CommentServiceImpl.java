package com.zerobase.munbanggu.community.service;

import com.zerobase.munbanggu.community.dto.CommentDto;
import com.zerobase.munbanggu.community.dto.CommentResponseDto;
import com.zerobase.munbanggu.community.model.entity.Comment;
import com.zerobase.munbanggu.community.model.entity.Community;
import com.zerobase.munbanggu.community.repository.CommentRepository;
import com.zerobase.munbanggu.community.repository.CommunityRepository;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, CommunityRepository communityRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.communityRepository = communityRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void addComment(Long communityId, CommentDto commentDto) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Community not found with id: " + communityId));

        // 현재 로그인한 사용자 정보 찾기, 로그인 구현 후 변경 필요
        User user = userRepository.findById(commentDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + commentDto.getUserId()));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setCommunity(community);
        comment.setContent(commentDto.getContent());
        comment.setCreatedDate(LocalDateTime.now());

        commentRepository.save(comment);
    }

    @Override
    public void addReply(Long commentId, CommentDto replyDto) {
        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));

        // 현재 로그인한 사용자 정보 찾기, 로그인 구현 후 변경 필요
        User user = userRepository.findById(replyDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + replyDto.getUserId()));

        Comment reply = new Comment();
        reply.setUser(user);
        reply.setCommunity(parentComment.getCommunity());
        reply.setContent(replyDto.getContent());
        reply.setParent(parentComment);
        reply.setCreatedDate(LocalDateTime.now());

        commentRepository.save(reply);
    }

    public void updateComment(Long commentId, CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));

        if (commentDto.getContent() != null) {
            comment.setContent(commentDto.getContent());
        }
        comment.setModifiedDate(LocalDateTime.now());
        commentRepository.save(comment);
    }

    public void updateReply(Long replyId, CommentDto replyDto) {
        Comment reply = commentRepository.findById(replyId)
                .orElseThrow(() -> new EntityNotFoundException("Reply not found with id: " + replyId));

        if (replyDto.getContent() != null) {
            reply.setContent(replyDto.getContent());
        }
        reply.setModifiedDate(LocalDateTime.now());
        commentRepository.save(reply);
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));
        for (Comment reply : comment.getReplies()) {
            reply.setParent(null);
            commentRepository.save(reply);
        }
        commentRepository.delete(comment);
    }

    public void deleteReply(Long replyId) {
        Comment reply = commentRepository.findById(replyId)
                .orElseThrow(() -> new EntityNotFoundException("Reply not found with id: " + replyId));
        commentRepository.delete(reply);
    }

    @Override
    public List<CommentResponseDto> getCommentsAndReplies(Long communityId) {
        List<Comment> comments = commentRepository.findByCommunityId(communityId);
        return comments.stream().map(comment -> {
            CommentResponseDto dto = new CommentResponseDto();
            dto.setUserNickname(comment.getUser().getNickname());
            dto.setContent(comment.getContent());
            dto.setCreatedDate(comment.getCreatedDate());
            dto.setModifiedDate(comment.getModifiedDate());
            return dto;
        }).collect(Collectors.toList());
    }
}