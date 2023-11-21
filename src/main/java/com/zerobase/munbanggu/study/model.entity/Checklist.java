package com.zerobase.munbanggu.study.model.entity;

import com.zerobase.munbanggu.study.type.AccessType;
import com.zerobase.munbanggu.user.model.entity.StudyUser;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Checklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "study_user_id")
    private StudyUser studyUser;

    private Long study_id;

    private Long user_id;

    @Builder.Default
    private boolean done = false; // 체크리스트 완료 여부

    private String todo; //할일

    @Enumerated(EnumType.STRING)
    private AccessType accessType;  //타입 - 스터디/개인

    @CreatedDate
    private LocalDateTime createdDate;
}
