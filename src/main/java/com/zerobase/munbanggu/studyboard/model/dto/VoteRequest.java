package com.zerobase.munbanggu.studyboard.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zerobase.munbanggu.validation.FutureDate;
import com.zerobase.munbanggu.validation.OptionSize;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoteRequest {

    @NotNull(message = "투표 제목은 필수 입력 사항입니다.")
    private String title;

    @OptionSize
    private List<VoteOptionRequest> options;

    @FutureDate
    private LocalDateTime endDate;

    @JsonCreator
    public VoteRequest(@JsonProperty("title") String title,
            @JsonProperty("options") List<VoteOptionRequest> options,
            @JsonProperty("endDate") LocalDateTime endDate) {

        this.title = title;
        this.options = options;
        this.endDate = endDate;
    }
}
