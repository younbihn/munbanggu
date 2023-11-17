package com.zerobase.munbanggu.studyboard.service;

import com.zerobase.munbanggu.studyboard.exception.NotFoundPostException;
import com.zerobase.munbanggu.studyboard.model.dto.PostRequest;
import com.zerobase.munbanggu.studyboard.model.dto.PostResponse;
import com.zerobase.munbanggu.studyboard.model.dto.VoteOptionRequest;
import com.zerobase.munbanggu.studyboard.model.entity.StudyBoardPost;
import com.zerobase.munbanggu.studyboard.model.entity.Vote;
import com.zerobase.munbanggu.studyboard.model.entity.VoteOption;
import com.zerobase.munbanggu.studyboard.repository.StudyBoardPostRepository;
import com.zerobase.munbanggu.studyboard.repository.VoteRepository;
import com.zerobase.munbanggu.studyboard.type.Type;
import com.zerobase.munbanggu.type.ErrorCode;
import com.zerobase.munbanggu.user.exception.NotFoundUserException;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyBoardService {

    private final StudyBoardPostRepository studyBoardPostRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostResponse create(PostRequest request) {
        StudyBoardPost post = buildPost(request);
        if (request.getType() == Type.VOTE) {
            Vote vote = buildVote(request, post);
            voteRepository.save(vote);
        }
        return PostResponse.from(studyBoardPostRepository.save(post));
    }

    public PostResponse update(PostRequest request, Long postId) {
        StudyBoardPost post = findPost(postId);
        post = updatePost(post, request);
        return PostResponse.from(studyBoardPostRepository.save(post));
    }

    public Page<PostResponse> search(String keyword, Pageable pageable) {
        Page<StudyBoardPost> postPage = studyBoardPostRepository.findByTitleContainingIgnoreCase(
                keyword, pageable);

        return postPage.map(PostResponse::from);
    }


    @Transactional
    public void delete(Long postId) {
        Optional<StudyBoardPost> optionalPost = studyBoardPostRepository.findById(postId);
        if (optionalPost.isPresent()) {
            studyBoardPostRepository.deleteById(postId);
        } else {
            throw new NotFoundPostException(ErrorCode.POST_NOT_FOUND);
        }
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(ErrorCode.NOT_FOUND_USER_ID));
    }

    private StudyBoardPost findPost(Long postId) {
        return studyBoardPostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundPostException(ErrorCode.POST_NOT_FOUND));
    }

    private Optional<Vote> findVote(Long voteId) {
        return voteRepository.findById(voteId);
    }

    private StudyBoardPost buildPost(PostRequest request) {
        User user = findUser(request.getUserId());

        return StudyBoardPost.builder()
                .type(request.getType())
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .build();
    }

    private Vote buildVote(PostRequest request, StudyBoardPost post) {
        Vote vote = Vote.builder()
                .title(request.getVote().getTitle())
                .endDate(request.getVote().getEndDate())
                .studyBoardPost(post)
                .build();
        List<VoteOptionRequest> optionRequests = request.getVote().getOptions();
        optionRequests.forEach(optionRequest -> {
            VoteOption option = VoteOption.builder()
                    .optionText(optionRequest.getOptionText())
                    .build();
            vote.addVoteOption(option);
        });
        return vote;
    }

    private StudyBoardPost updatePost(StudyBoardPost post, PostRequest request) {
        post.setType(request.getType());
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        if (request.getType() == Type.VOTE) {
            Vote newVote = buildVote(request, post);
            newVote = voteRepository.save(newVote);
            post.setVote(newVote);
        }
        return post;
    }

    private void updateVoteOptions(Long voteId, List<VoteOptionRequest> newOptions) {
        Vote vote = findVote(voteId).orElseThrow(() -> new NotFoundPostException(ErrorCode.VOTE_NOT_FOUND));
        List<VoteOption> existingOptions = vote.getOptions();

        List<String> existingOptionTexts = existingOptions.stream()
                .map(VoteOption::getOptionText) //voteOption -> voteOption.getOptionText()
                .collect(Collectors.toList());

        List<String> newOptionTexts = newOptions.stream().map(VoteOptionRequest::getOptionText)
                .collect(Collectors.toList());

        handleAdditions(vote, newOptionTexts, existingOptionTexts);
        handleRemovals(existingOptions, existingOptionTexts, newOptionTexts);

        voteRepository.save(vote);
    }

    private void handleAdditions(Vote vote, List<String> newOptionTexts, List<String> existingOptionTexts) {
        List<String> additions = new ArrayList<>(newOptionTexts);
        additions.removeAll(existingOptionTexts);

        for (String addition : additions) {
            VoteOption voteOption = VoteOption.builder()
                    .optionText(addition)
                    .vote(vote)
                    .build();
            vote.addVoteOption(voteOption);
        }
    }

    private void handleRemovals(List<VoteOption> existingOptions, List<String> existingOptionTexts,
            List<String> newOptionTexts) {
        List<String> removals = new ArrayList<>(existingOptionTexts);
        removals.removeAll(newOptionTexts);
        existingOptions.removeIf(option -> removals.contains(option.getOptionText()));
    }
}
