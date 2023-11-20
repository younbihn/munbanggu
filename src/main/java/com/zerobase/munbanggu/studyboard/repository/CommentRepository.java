package com.zerobase.munbanggu.studyboard.repository;

import com.zerobase.munbanggu.studyboard.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByStudyBoardPostId(Long postId, Pageable pageable);
}
