package com.zerobase.munbanggu.community.dto;

import com.zerobase.munbanggu.user.type.CommunityCategoty;

import java.util.Set;

public class CommunityUpdateDto {
    private String title;
    private String content;

    public CommunityCategoty getCommunityCategoty() {
        return communityCategoty;
    }

    public void setCommunityCategoty(CommunityCategoty communityCategoty) {
        this.communityCategoty = communityCategoty;
    }

    private CommunityCategoty communityCategoty;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(Set<String> hashtags) {
        this.hashtags = hashtags;
    }

    private Set<String> hashtags;
}
