package com.zerobase.munbanggu.study.dto;

import com.zerobase.munbanggu.user.type.EnrollmentStatus;
import com.zerobase.munbanggu.user.type.RefundCycle;
import java.time.LocalDateTime;
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
public class StudyDto {

    private String userId;

    private String title;

    private String content;

    private long minUser;

    private long maxUser;

    private boolean publicOrNot;

    private String password;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private boolean startRule;

    private boolean startAttendOrNot;

    private long checklistCycle;

    private long fee;

    private RefundCycle refundCycle;

    private EnrollmentStatus status;
}