package com.zerobase.munbanggu.community.service;

import com.zerobase.munbanggu.community.dto.CommunityCreateDto;
import com.zerobase.munbanggu.community.dto.CommunityResponseDto;
import com.zerobase.munbanggu.community.dto.CommunityUpdateDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommunityService {

    void createPost(CommunityCreateDto communityCreateDto);
    void updatePost(Long id, CommunityUpdateDto updateDto);
    void deletePost(Long id);
    CommunityResponseDto getPost(Long id);
    List<CommunityResponseDto> searchPosts(String title, String content);
    List<CommunityResponseDto> getCommunitiesByHashtag(String hashtag);
}
