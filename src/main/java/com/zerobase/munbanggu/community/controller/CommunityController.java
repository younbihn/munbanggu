package com.zerobase.munbanggu.community.controller;

import com.zerobase.munbanggu.community.dto.CommunityCreateDto;
import com.zerobase.munbanggu.community.dto.CommunityResponseDto;
import com.zerobase.munbanggu.community.dto.CommunityUpdateDto;
import com.zerobase.munbanggu.community.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityService communityService;

    @Autowired
    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @PostMapping("/post")
    public ResponseEntity<String> createPost(@RequestBody CommunityCreateDto communityCreateDto) {
        communityService.createPost(communityCreateDto);
        return ResponseEntity.ok("커뮤니티 게시글이 작성 되었습니다.");
    }

    @PatchMapping("/post/{id}")
    public ResponseEntity<String> updatePost(@PathVariable Long id, @RequestBody CommunityUpdateDto communityUpdateDto) {
        communityService.updatePost(id, communityUpdateDto);
        return ResponseEntity.ok("커뮤니티 게시글이 수정되었습니다.");
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        communityService.deletePost(id);
        return ResponseEntity.ok("커뮤니티 게시글이 삭제되었습니다.");
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<CommunityResponseDto> getPost(@PathVariable Long id) {
        CommunityResponseDto communityResponseDto = communityService.getPost(id);
        return ResponseEntity.ok(communityResponseDto);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CommunityResponseDto>> searchPosts(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content) {
        List<CommunityResponseDto> results = communityService.searchPosts(title, content);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/search/by-hashtag")
    public ResponseEntity<List<CommunityResponseDto>> getCommunitiesByHashtag(@RequestParam String hashtag) {
        List<CommunityResponseDto> communities = communityService.getCommunitiesByHashtag(hashtag);
        return ResponseEntity.ok(communities);
    }
}