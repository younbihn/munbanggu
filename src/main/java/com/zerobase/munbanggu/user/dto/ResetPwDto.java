package com.zerobase.munbanggu.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPwDto {
  private String token;
  private String inputCode;
  private String newPassword;

}
