package com.zerobase.munbanggu.study.repository;

import com.zerobase.munbanggu.study.model.entity.Checklist;
import com.zerobase.munbanggu.study.model.entity.Study;
import com.zerobase.munbanggu.study.type.AccessType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChecklistRepository extends JpaRepository<Checklist,Long> {

  List<Checklist> findByStudyIn(List<Study> studyIds);
  List<Checklist> findByUserIdAndAccessType(Long userId, AccessType accessType);
  List<Checklist> findByStudyAndAccessType(Study study, AccessType accessType);
}
