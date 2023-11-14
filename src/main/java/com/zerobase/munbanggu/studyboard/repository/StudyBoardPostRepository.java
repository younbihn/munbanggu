package com.zerobase.munbanggu.studyboard.repository;

import com.zerobase.munbanggu.studyboard.model.entity.StudyBoardPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyBoardPostRepository extends JpaRepository<StudyBoardPost, Long> {

Page<StudyBoardPost> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
}
