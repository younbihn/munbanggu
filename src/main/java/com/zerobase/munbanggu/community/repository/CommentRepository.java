package com.zerobase.munbanggu.community.repository;

import com.zerobase.munbanggu.community.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByCommunityIdAndParentIsNull(Long communityId);
    List<Comment> findByCommunityId (Long communityId);
}