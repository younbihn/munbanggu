package com.zerobase.munbanggu.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class GetUserDto {

  private String email;
  private String nickname;
  private String phone;
  private String profileImageUrl;
}
