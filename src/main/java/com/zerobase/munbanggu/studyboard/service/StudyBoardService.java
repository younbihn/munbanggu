package com.zerobase.munbanggu.studyboard.service;

import com.zerobase.munbanggu.auth.TokenProvider;
import com.zerobase.munbanggu.study.exception.NotFoundStudyException;
import com.zerobase.munbanggu.study.model.entity.Study;
import com.zerobase.munbanggu.study.model.entity.StudyMember;
import com.zerobase.munbanggu.study.repository.StudyMemberRepository;
import com.zerobase.munbanggu.study.repository.StudyRepository;
import com.zerobase.munbanggu.studyboard.exception.NoPermissionException;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyBoardService {

    private final StudyBoardPostRepository studyBoardPostRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final TokenProvider tokenProvider;
    private final StudyMemberRepository studyMemberRepository;

    @Transactional
    public PostResponse create(PostRequest request, Long studyId, String token) {
        StudyBoardPost post = buildPost(request, studyId, token);
        Long userId = tokenProvider.getId(token);
        StudyBoardPost newPost = studyBoardPostRepository.save(post);
        if (request.getType() == Type.VOTE) {
            Vote vote = buildVote(request, post);
            voteRepository.save(vote);
        }
        if (!existStudyMember(studyId, userId)) {
            throw new NoPermissionException(ErrorCode.NOT_FOUND_STUDY_MEMBER);
        }
        return PostResponse.from(newPost);
    }

    public PostResponse update(PostRequest request, Long studyId, Long postId, String token) {
        StudyBoardPost post = findPost(postId);
        findStudy(studyId);
        Long userId = tokenProvider.getId(token);

        if (!existStudyMember(studyId, userId)) {
            throw new NoPermissionException(ErrorCode.NOT_FOUND_STUDY_MEMBER);
        }

        if (!userId.equals(post.getUser().getId())) {
            log.error("user id {} 수정 시도 / post id {} user id {}");
            throw new NoPermissionException(ErrorCode.NO_PERMISSION_TO_MODIFY);
        }
        post = updatePost(post, request);
        return PostResponse.from(studyBoardPostRepository.save(post));
    }

    public Page<PostResponse> search(String keyword, Pageable pageable) {
        Page<StudyBoardPost> postPage = studyBoardPostRepository.findByTitleContainingIgnoreCase(
                keyword, pageable);

        return postPage.map(PostResponse::from);
    }


    @Transactional
    public void delete(Long studyId, Long postId, String token) {
        findStudy(studyId);
        Long userId = tokenProvider.getId(token);
        StudyBoardPost post = findPost(postId);

        if (!existStudyMember(studyId, userId)) {
            throw new NoPermissionException(ErrorCode.NOT_FOUND_STUDY_MEMBER);
        }
        if (!userId.equals(post.getUser().getId())) {
            log.error("user id {} 삭제 시도 / post id {} user id {}");
            throw new NoPermissionException(ErrorCode.NO_PERMISSION_TO_MODIFY);
        }
        Optional<StudyBoardPost> optionalPost = studyBoardPostRepository.findById(postId);
        if (optionalPost.isPresent()) {
            studyBoardPostRepository.deleteById(postId);
        } else {
            throw new NotFoundPostException(ErrorCode.NOT_FOUND_POST);
        }
    }

    private boolean existStudyMember(Long studyId, Long userId) {
        List<StudyMember> studyMembers = studyMemberRepository.findByStudyId(studyId);
        List<StudyMember> collect = studyMembers.stream()
                .filter(studyMember -> studyMember.getUser().getId().equals(userId)).collect(Collectors.toList());
        return !collect.isEmpty();
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(ErrorCode.NOT_FOUND_USER_ID));
    }

    private StudyBoardPost findPost(Long postId) {
        return studyBoardPostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundPostException(ErrorCode.NOT_FOUND_POST));
    }

    private Study findStudy(Long studyId) {
        return studyRepository.findById(studyId)
                .orElseThrow(() -> new NotFoundStudyException(ErrorCode.NOT_FOUND_STUDY));
    }

    private StudyBoardPost buildPost(PostRequest request, Long studyId, String token) {
        Long userId = tokenProvider.getId(token);
        User user = findUser(userId);
        Study study = findStudy(studyId);
        return StudyBoardPost.builder()
                .type(request.getType())
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .study(study)
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
}
