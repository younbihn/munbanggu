package com.zerobase.munbanggu.studyboard.model.dto;


import com.zerobase.munbanggu.studyboard.model.entity.Comment;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommentResponse {

    private Long id;

    private String content;

    private Long userId;

    private String nickname;

    private LocalDateTime createdDate;

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUser().getId())
                .nickname(comment.getUser().getNickname())
                .createdDate(comment.getCreatedDate())
                .build();

    }
}
