package com.zerobase.munbanggu.user.model.entity;

import com.zerobase.munbanggu.study.model.entity.Checklist;
import com.zerobase.munbanggu.study.model.entity.Study;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class StudyUser {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "study_id")
  private Study study;

  @CreatedDate
  @Column(name = "certification_date")
  private LocalDateTime latestCertificationDate;

  @OneToMany(mappedBy = "studyUser")
  private List<Checklist> checklists;


  public StudyUser(User user,Study study, LocalDateTime latestCertificationDate){
    this.user = user;
    this.study = study;
    this.latestCertificationDate = latestCertificationDate;
    this.checklists = new ArrayList<>(); // Checklist 초기화
  }
}
