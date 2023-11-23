package com.zerobase.munbanggu.community.dto;

import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.type.CommunityCategoty;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class CommunityCreateDto {
    private Long user;
    private String title;
    private String content;

    public CommunityCategoty getCommunityCategoty() {
        return communityCategoty;
    }

    public void setCommunityCategoty(CommunityCategoty communityCategoty) {
        this.communityCategoty = communityCategoty;
    }

    private CommunityCategoty communityCategoty;


    public void setHashtags(Set<String> hashtags) {
        this.hashtags = hashtags;
    }

    public Set<String> getHashtags() {
        return hashtags;
    }

    private Set<String> hashtags = new HashSet<>();


    public void setUser(Long user) {
        this.user = user;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
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

    private String photourl;

    public Long getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getPhotourl() {
        return photourl;
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

    private Long view;
    private LocalDateTime created_date;
    private LocalDateTime modified_date;
}
