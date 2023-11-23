package com.zerobase.munbanggu.community.dto;

import com.zerobase.munbanggu.user.type.CommunityCategoty;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class CommunityResponseDto {
    public Set<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(Set<String> hashtags) {
        this.hashtags = hashtags;
    }

    private Set<String> hashtags = new HashSet<>();

    public Long getId() {
        return id;
    }

    public Long getUser() {
        return user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCommunityCategoty(CommunityCategoty communityCategoty) {
        this.communityCategoty = communityCategoty;
    }

    public void setView(Long view) {
        this.view = view;
    }

    public void setCreated_date(LocalDateTime created_date) {
        this.created_date = created_date;
    }

    public void setModified_date(LocalDateTime modified_date) {
        this.modified_date = modified_date;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public CommunityCategoty getCommunityCategoty() {
        return communityCategoty;
    }

    public Long getView() {
        return view;
    }

    public LocalDateTime getCreated_date() {
        return created_date;
    }

    public LocalDateTime getModified_date() {
        return modified_date;
    }

    private Long id;
    private Long user;
    private String title;
    private String content;
    private CommunityCategoty communityCategoty;
    private Long view;
    private LocalDateTime created_date;
    private LocalDateTime modified_date;

}
