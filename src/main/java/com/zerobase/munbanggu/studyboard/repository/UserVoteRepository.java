package com.zerobase.munbanggu.studyboard.repository;

import com.zerobase.munbanggu.studyboard.model.entity.UserVote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserVoteRepository extends JpaRepository<UserVote, Long> {

    boolean existsByUserIdAndVoteId(Long userId, Long voteId);
}
