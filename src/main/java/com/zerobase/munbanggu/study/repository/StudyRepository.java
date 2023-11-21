package com.zerobase.munbanggu.study.repository;

import com.zerobase.munbanggu.study.model.entity.Study;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {

  @Query("SELECT s FROM Study s WHERE s.user_id = :userId")
  List<Study> findStudyIdByUserId(Long userId);
  List<Study> findAllByContent(String content);
  
}

