package com.zerobase.munbanggu.user.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    INACTIVE("ROLE_INACTIVE");

    private final String key;

}