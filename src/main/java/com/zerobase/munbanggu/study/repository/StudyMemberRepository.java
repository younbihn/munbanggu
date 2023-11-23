package com.zerobase.munbanggu.study.repository;

import com.zerobase.munbanggu.study.model.entity.StudyMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {
    List<StudyMember> findByStudyId(Long studyId);
}
