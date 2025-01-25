package com.zerobase.munbanggu.user.type;

public enum CommunityCategoty {
    DAILY("일상"),
    QUESTION("질문");

    private final String label;

    CommunityCategoty(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}