package com.zerobase.munbanggu.studyboard.controller;

import com.zerobase.munbanggu.auth.TokenProvider;
import com.zerobase.munbanggu.studyboard.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;
    private final TokenProvider tokenProvider;

    /**
     * 투표 (하나의 투표는 한 번만 가능, 완료 후 재시도할 경우 예외 발생)
     * @param studyId 스터디 아이디
     * @param voteId 게시글 내 생성된 투표 아이디 (게시글 생성 시 반환되는 값)
     * @param optionId 투표 내 선택 항목 아이디 (게시글 생성 시 반환되는 값)
     * @param authHeader Bearer 방식 JWT
     * @return
     */
    @PostMapping("/{study_id}/vote/{vote_id}/vote")
    public ResponseEntity<String> vote(@PathVariable("study_id") Long studyId, @PathVariable("vote_id") Long voteId,
            @RequestParam("option") Long optionId, @RequestHeader(name = "Authorization") String authHeader) {
        String token = tokenProvider.getRawToken(authHeader);
        voteService.vote(studyId, voteId, optionId, token);
        return ResponseEntity.ok().body("투표되었습니다.");
    }
}
