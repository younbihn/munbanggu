package com.zerobase.munbanggu.community.repository;

import com.zerobase.munbanggu.community.model.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long> {
    List<Community> findByTitleContainingOrContentContaining(String title, String content);
    @Query("SELECT c.id FROM Community c JOIN c.hashtags h WHERE h = :hashtag")
    List<Long> findCommunityIdsByHashtag(@Param("hashtag") String hashtag);
}