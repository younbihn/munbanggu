package com.zerobase.munbanggu.studyboard.service;

import static org.junit.jupiter.api.Assertions.*;

import com.zerobase.munbanggu.studyboard.exception.AlreadyVotedException;
import com.zerobase.munbanggu.studyboard.model.entity.Vote;
import com.zerobase.munbanggu.studyboard.model.entity.VoteOption;
import com.zerobase.munbanggu.studyboard.repository.UserVoteRepository;
import com.zerobase.munbanggu.studyboard.repository.VoteOptionRepository;
import com.zerobase.munbanggu.studyboard.repository.VoteRepository;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.repository.UserRepository;
import com.zerobase.munbanggu.user.type.Role;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@Transactional
@TestPropertySource("classpath:test-application.yml")
class VoteServiceTest {

    @Autowired
    private VoteService voteService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VoteOptionRepository voteOptionRepository;

    @Autowired
    private UserVoteRepository userVoteRepository;

    @Test
    public void testVote() {
        // Given
        User newUser = User.builder()
                .email("test4321@gmail.com")
                .password("$2a$10$La/a.8KB1Ltx7yNgfNnKu.3sfmzXmT12zL0KLfj5567M0Y/Kanqwq")
                .nickname("nick")
                .role(Role.USER)
                .build();

        Vote newVote = Vote.builder()
                .title("투표제목")
                .build();
        VoteOption newOption1 = VoteOption.builder().optionText("옵션 1").vote(newVote)
                .build();
        VoteOption newOption2 = VoteOption.builder().optionText("옵션 2").vote(newVote)
                .build();
        newVote.addVoteOption(newOption1);
        newVote.addVoteOption(newOption2);

        User user = userRepository.save(newUser);
        Vote vote = voteRepository.save(newVote);
        VoteOption option = voteOptionRepository.save(newOption1);
        VoteOption option2 = voteOptionRepository.save(newOption2);

        // When
        voteService.vote(user.getId(), vote.getId(), option.getId());

        // Then
        assertTrue(userVoteRepository.existsByUserIdAndVoteId(user.getId(), vote.getId()));
    }

    @Test
    void testVoteTwiceForSameOption() {
        // Given
        User newUser = User.builder()
                .email("test5678@gmail.com")
                .password("$2a$10$La/a.8KB1Ltx7yNgfNnKu.3sfmzXmT12zL0KLfj5567M0Y/Kanqwq")
                .nickname("nick")
                .role(Role.USER)
                .build();

        Vote newVote = Vote.builder()
                .title("투표하세요")
                .build();
        VoteOption newOption1 = VoteOption.builder().optionText("옵션 1").vote(newVote)
                .build();

        newVote.addVoteOption(newOption1);
        User user = userRepository.save(newUser);
        Vote vote = voteRepository.save(newVote);
        VoteOption option = voteOptionRepository.save(newOption1);

        // When
        voteService.vote(user.getId(), vote.getId(), option.getId());

        // Then
        assertThrows(AlreadyVotedException.class, () ->
                voteService.vote(user.getId(), vote.getId(), option.getId()));
    }

}