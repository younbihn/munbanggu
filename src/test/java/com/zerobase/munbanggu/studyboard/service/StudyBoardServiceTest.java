//package com.zerobase.munbanggu.studyboard.service;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;
//
//import com.zerobase.munbanggu.studyboard.model.dto.PostRequest;
//import com.zerobase.munbanggu.studyboard.model.dto.PostResponse;
//import com.zerobase.munbanggu.studyboard.model.dto.VoteOptionRequest;
//import com.zerobase.munbanggu.studyboard.model.dto.VoteRequest;
//import com.zerobase.munbanggu.studyboard.model.entity.StudyBoardPost;
//import com.zerobase.munbanggu.studyboard.model.entity.Vote;
//import com.zerobase.munbanggu.studyboard.model.entity.VoteOption;
//import com.zerobase.munbanggu.studyboard.repository.StudyBoardPostRepository;
//import com.zerobase.munbanggu.studyboard.repository.VoteRepository;
//import com.zerobase.munbanggu.user.model.entity.User;
//import com.zerobase.munbanggu.user.repository.UserRepository;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@RunWith(MockitoJUnitRunner.class)
//@SpringBootTest
//class StudyBoardServiceTest {
//
//    @InjectMocks
//    private StudyBoardService studyBoardService;
//
//    @Mock
//    private StudyBoardPostRepository studyBoardPostRepository;
//
//    @Mock
//    private VoteRepository voteRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Test
//    void testCreate() {
//        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(mockUser()));
//        when(voteRepository.save(any())).thenReturn(mockVote());
//
//        List<VoteOptionRequest> optionRequests = new ArrayList<>();
//        VoteOptionRequest optionRequest = new VoteOptionRequest();
//        optionRequest.setOptionText("option1");
//        optionRequests.add(optionRequest);
//        optionRequest = new VoteOptionRequest();
//        optionRequest.setOptionText("option2");
//        optionRequests.add(optionRequest);
//
//        String dateTimeAsString = "2023-11-11T11:00:00";
//        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
//        LocalDateTime localDateTime = java.time.LocalDateTime.parse(dateTimeAsString, formatter);
//
//        VoteRequest voteRequest = new VoteRequest("투표 제목", optionRequests, localDateTime);
//        PostRequest request = PostRequest.builder()
//                .title("게시글 제목")
//                .content("게시글 내용")
//                .vote(voteRequest)
//                .build();
//
////        PostResponse response = studyBoardService.create(1, request);
////
////        Assertions.assertThat(response.getTitle()).isEqualTo("게시글 제목");
//    }
//
//    private User mockUser() {
//        User user = Mockito.mock(User.class);
//        when(user.getId()).thenReturn(1L);
//        when(user.getEmail()).thenReturn("test@gmail.com");
//        return user;
//    }
//
//    private StudyBoardPost mockPost() {
//        StudyBoardPost post = mock(StudyBoardPost.class);
//        when(post.getId()).thenReturn(1L);
//        when(post.getTitle()).thenReturn("Mock Post Title");
//        when(post.getContent()).thenReturn("Mock Post Content");
//
//        Vote mockVote = mock(Vote.class);
//        when(mockVote.getId()).thenReturn(1L);
//        when(mockVote.getTitle()).thenReturn("Mock Vote Title");
//
//        VoteOption mockOption1 = mock(VoteOption.class);
//        when(mockOption1.getId()).thenReturn(1L);
//        when(mockOption1.getOptionText()).thenReturn("Mock Option 1");
//
//        VoteOption mockOption2 = mock(VoteOption.class);
//        when(mockOption1.getId()).thenReturn(2L);
//        when(mockOption1.getOptionText()).thenReturn("Mock Option 2");
//
//        when(mockVote.getOptions()).thenReturn(Arrays.asList(mockOption1, mockOption2));
//
//        when(mockPost().getVote()).thenReturn(mockVote);
//
//        return post;
//    }
//
//    private Vote mockVote() {
//        Vote vote = mock(Vote.class);
//        when(vote.getId()).thenReturn(2L);
//        when(vote.getTitle()).thenReturn("mock Vote Title 2");
//        when(vote.getEndDate()).thenReturn(LocalDateTime.parse("2023-11-29T00:00:00"));
//        return vote;
//    }
////
////    @Autowired
////    private StudyBoardService studyBoardService;
////
////    @Autowired
////    private StudyBoardPostRepository studyBoardPostRepository;
////
////    @Test
////    void testStudyBoardPostVote() {
////
////        // given
////        List<VoteOptionRequest> optionRequests = new ArrayList<>();
////        VoteOptionRequest optionRequest = new VoteOptionRequest();
////        optionRequest.setOptionText("option1");
////        optionRequests.add(optionRequest);
////        optionRequest = new VoteOptionRequest();
////        optionRequest.setOptionText("option2");
////        optionRequests.add(optionRequest);
////
////        String dateTimeAsString = "2023-11-11T11:00:00";
////        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
////        LocalDateTime localDateTime = java.time.LocalDateTime.parse(dateTimeAsString, formatter);
////
////        VoteRequest voteRequest = new VoteRequest("투표 제목", optionRequests, localDateTime);
////        PostRequest request = PostRequest.builder()
////                .title("게시글 제목")
////                .content("게시글 내용")
////                .vote(voteRequest)
////                .userId(1L)
////                .build();
////
////        // when
////        PostResponse response = studyBoardService.create(request);
////
////        // then
////        Assertions.assertThat(response.getTitle()).isEqualTo("게시글 제목");
////
////    }
////
////    @Test
////    void testStudyBoardModification() {
////        // given
////        List<VoteOptionRequest> optionRequests = new ArrayList<>();
////        VoteOptionRequest optionRequest = new VoteOptionRequest();
////        optionRequest.setOptionText("option1");
////        optionRequests.add(optionRequest);
////        optionRequest = new VoteOptionRequest();
////        optionRequest.setOptionText("option2");
////        optionRequest = new VoteOptionRequest();
////        optionRequest.setOptionText("옵션2");
////        optionRequest = new VoteOptionRequest();
////        optionRequest.setOptionText("옵션3");
////        optionRequests.add(optionRequest);
////
////        String dateTimeAsString = "2023-11-13T11:00:00";
////        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
////        LocalDateTime localDateTime = java.time.LocalDateTime.parse(dateTimeAsString, formatter);
////
////        VoteRequest voteRequest = new VoteRequest("투표 제목", optionRequests, localDateTime);
////        PostRequest request = PostRequest.builder()
////                .title("게시글 제목")
////                .content("게시글 내용")
////                .vote(voteRequest)
////                .userId(1L)
////                .build();
////
////        // when
////        PostResponse response = studyBoardService.update(request, 1L);
////
////        // then
////        Assertions.assertThat(response.getTitle()).isEqualTo("게시글 제목");
////    }
//
//}