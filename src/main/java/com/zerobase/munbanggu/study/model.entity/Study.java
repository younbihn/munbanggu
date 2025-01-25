package com.zerobase.munbanggu.study.model.entity;

import com.zerobase.munbanggu.study.type.ChecklistCycle;
import com.zerobase.munbanggu.study.type.EnrollmentStatus;
import com.zerobase.munbanggu.study.type.RefundCycle;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import reactor.util.annotation.Nullable;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String title;

    private String content;

    private long min_user;

    private long max_user;

    private boolean public_or_not;

    @Nullable
    private String password;

    private LocalDateTime start_date;

    private LocalDateTime end_date;

    @Nullable
    private boolean start_rule;

    @Nullable
    private boolean start_attend_or_not;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ChecklistCycle checklist_cycle;

    private long fee;

    @Enumerated(EnumType.STRING)
    @NotNull
    private RefundCycle refundCycle;

    @CreatedDate
    private LocalDateTime latest_refund_date;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

    @CreatedDate
    private LocalDateTime createDate;

    @Nullable
    private LocalDateTime deleteDate;

}
