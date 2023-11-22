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

    @PostMapping("/{study_id}/vote/{vote_id}/vote")
    public ResponseEntity<?> vote(@PathVariable("study_id") Long studyId, @PathVariable("vote_id") Long voteId,
            @RequestParam("option") Long optionId, @RequestHeader(name = "Authorization") String authorizationHeader) {
//        String token = authorizationHeader.replace("Bearer ", "");
//        Long userId = tokenProvider.getId(token);
        Long userId = 1L;
        voteService.vote(userId, voteId, optionId);
        return ResponseEntity.ok().build();
    }
}
