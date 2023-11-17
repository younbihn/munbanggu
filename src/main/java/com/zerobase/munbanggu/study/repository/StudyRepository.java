package com.zerobase.munbanggu.study.repository;

import com.zerobase.munbanggu.study.model.entity.Study;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {

    List<Study> findAllByContent(String content);
}
