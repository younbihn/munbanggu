package com.zerobase.munbanggu.community.service;

import com.zerobase.munbanggu.community.dto.CommunityCreateDto;
import com.zerobase.munbanggu.community.dto.CommunityResponseDto;
import com.zerobase.munbanggu.community.dto.CommunityUpdateDto;
import com.zerobase.munbanggu.community.model.entity.Community;
import com.zerobase.munbanggu.community.repository.CommunityRepository;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommunityServiceImpl implements CommunityService {
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommunityServiceImpl(CommunityRepository communityRepository, UserRepository userRepository) {
        this.communityRepository = communityRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void createPost(CommunityCreateDto communityCreateDto) {
//        // 현재 로그인한 유저의 정보 가져오기
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Object principal = authentication.getPrincipal();
//
//        String currentUseremail;
//
//        if (principal instanceof UserDetails) {
//            currentUseremail = ((UserDetails) principal).getUsername();
//        } else {
//            currentUseremail = principal.toString();
//        }
//
//        // 로그인한 유저의 정보로 조회
//        User user = userRepository.findByEmail(currentUseremail)
//                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + currentUseremail));
        User user = userRepository.findById(communityCreateDto.getUser())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + communityCreateDto.getUser()));


        Community community = Community.builder()
                .title(communityCreateDto.getTitle())
                .communityCategoty(communityCreateDto.getCommunityCategoty())
                .content(communityCreateDto.getContent())
                .user(user)
                .hashtags(communityCreateDto.getHashtags())
                .build();
        communityRepository.save(community);
    }

    @Override
    public void updatePost(Long id, CommunityUpdateDto communityUpdateDto) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Community not found with id: " + id));

        if(communityUpdateDto.getTitle() != null) {
            community.setTitle(communityUpdateDto.getTitle());
        }
        if(communityUpdateDto.getContent() != null) {
            community.setContent(communityUpdateDto.getContent());
        }
        if(communityUpdateDto.getHashtags() != null) {
            community.setHashtags(communityUpdateDto.getHashtags());
        }
        if(communityUpdateDto.getCommunityCategoty() != null) {
            community.setCommunityCategoty(communityUpdateDto.getCommunityCategoty());
        }

        communityRepository.save(community);
    }

    @Override
    public void deletePost(Long id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Community not found with id: " + id));

        communityRepository.delete(community);
    }

    @Override
    public CommunityResponseDto getPost(Long id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Community not found with id: " + id));

        CommunityResponseDto communityResponseDto = new CommunityResponseDto();
        communityResponseDto.setId(community.getId());
        communityResponseDto.setUser(community.getUser().getId());
        communityResponseDto.setTitle(community.getTitle());
        communityResponseDto.setContent(community.getContent());
        communityResponseDto.setCommunityCategoty(community.getCommunityCategoty());
        communityResponseDto.setView(community.getView());
        communityResponseDto.setCreated_date(community.getCreated_date());
        communityResponseDto.setModified_date(community.getModified_date());
        communityResponseDto.setHashtags(community.getHashtags());

        community.setView(community.getView() + 1);
        communityRepository.save(community);

        return communityResponseDto;
    }

    @Override
    public List<CommunityResponseDto> searchPosts(String title, String content) {
        List<Community> communities = communityRepository.findByTitleContainingOrContentContaining(title, content);
        return communities.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private CommunityResponseDto convertToDto(Community community) {
        CommunityResponseDto dto = new CommunityResponseDto();
        dto.setId(community.getId());
        dto.setUser(community.getUser().getId());
        dto.setTitle(community.getTitle());
        dto.setContent(community.getContent());
        dto.setCommunityCategoty(community.getCommunityCategoty());
        dto.setView(community.getView());
        dto.setCreated_date(community.getCreated_date());
        dto.setModified_date(community.getModified_date());
        dto.setHashtags(community.getHashtags());
        return dto;
    }

    @Override
    public List<CommunityResponseDto> getCommunitiesByHashtag(String hashtag) {
        List<Long> communityIds = communityRepository.findCommunityIdsByHashtag(hashtag);
        return communityIds.stream()
                .map(this::getPost)
                .collect(Collectors.toList());
    }
}