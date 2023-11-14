package com.zerobase.munbanggu.studyboard.repository;

import com.zerobase.munbanggu.studyboard.model.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {

}
