package com.zerobase.munbanggu.studyboard.model.dto;

import com.zerobase.munbanggu.studyboard.type.Type;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {

    @NotNull(message = "게시글 유형은 필수 입력 사항입니다.")
    private Type type;

    @NotNull(message = "게시글 제목은 필수 입력 사항입니다.")
    private String title;

    @NotNull(message = "게시글 내용은 필수 입력 사항입니다.")
    private String content;

    @Valid
    private VoteRequest vote;
}
