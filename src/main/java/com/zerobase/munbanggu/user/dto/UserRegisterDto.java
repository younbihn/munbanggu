package com.zerobase.munbanggu.user.dto;


import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.type.AuthProvider;
import com.zerobase.munbanggu.user.type.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterDto {
    private String name;
    private String email;
    private String password;
    private String nickname;
    private String phone;
    private String profileImageUrl;
}
