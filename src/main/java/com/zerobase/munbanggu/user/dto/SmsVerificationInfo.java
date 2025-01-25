package com.zerobase.munbanggu.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsVerificationInfo {
  private String email;
  private String phone;
  private String verificationCode;
  private String token;

}
