package com.zerobase.munbanggu.studyboard.model.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentRequest {

    private String content;
    private Long parentId;
}
