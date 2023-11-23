package com.zerobase.munbanggu.studyboard.service;

import static com.zerobase.munbanggu.common.type.ErrorCode.ALREADY_VOTED;
import static com.zerobase.munbanggu.common.type.ErrorCode.NOT_FOUND_OPTION;
import static com.zerobase.munbanggu.common.type.ErrorCode.NOT_FOUND_USER_ID;
import static com.zerobase.munbanggu.common.type.ErrorCode.NOT_FOUND_VOTE;

import com.zerobase.munbanggu.common.exception.AlreadyVotedException;
import com.zerobase.munbanggu.common.exception.NotFoundPostException;
import com.zerobase.munbanggu.common.exception.NotFoundUserException;
import com.zerobase.munbanggu.studyboard.model.entity.UserVote;
import com.zerobase.munbanggu.studyboard.model.entity.Vote;
import com.zerobase.munbanggu.studyboard.model.entity.VoteOption;
import com.zerobase.munbanggu.studyboard.repository.UserVoteRepository;
import com.zerobase.munbanggu.studyboard.repository.VoteOptionRepository;
import com.zerobase.munbanggu.studyboard.repository.VoteRepository;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteService {

    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final UserVoteRepository userVoteRepository;

    @Transactional
    public void vote(Long userId, Long voteId, Long optionId) {

        User user = findUser(userId);
        Vote vote = findVote(voteId);
        VoteOption selectedOption = findOption(optionId);
        log.info("투표 시작 - User : {}, Vote : {}, Option: {}", userId, voteId, optionId);

        if (hasUserVoted(userId, voteId)) {
            log.warn("User {} 이미 Vote {} 에 투표함", userId, voteId);
            throw new AlreadyVotedException(ALREADY_VOTED);
        }

        UserVote userVote = UserVote.builder()
                .user(user)
                .vote(vote)
                .voteOption(selectedOption).build();
        userVoteRepository.save(userVote);
        log.info("투표 성공 - User : {}, Vote : {}, Option: {}", userId, voteId, optionId);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(NOT_FOUND_USER_ID));
    }

    private Vote findVote(Long voteId) {
        return voteRepository.findById(voteId).orElseThrow(() -> new NotFoundPostException(NOT_FOUND_VOTE));
    }

    private VoteOption findOption(Long optionId) {
        return voteOptionRepository.findById(optionId)
                .orElseThrow(() -> new NotFoundPostException(NOT_FOUND_OPTION));
    }

    private boolean hasUserVoted(Long userId, Long voteId) {
        return userVoteRepository.existsByUserIdAndVoteId(userId, voteId);
    }

}
