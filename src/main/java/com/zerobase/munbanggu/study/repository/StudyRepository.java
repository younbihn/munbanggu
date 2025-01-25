package com.zerobase.munbanggu.study.repository;

import com.zerobase.munbanggu.study.model.entity.Study;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {
  List<Study> findStudyIdByUserId(Long userId);
  List<Study> findAllByContent(String content);
  
}

