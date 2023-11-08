package com.zerobase.munbanggu.user.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDto {
    private String email;

    private String nickname;

    private String phone;

    private String profileImageUrl;
}
