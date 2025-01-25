package com.zerobase.munbanggu.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailVerificationDto {

  private String email;
  private String token;

}
