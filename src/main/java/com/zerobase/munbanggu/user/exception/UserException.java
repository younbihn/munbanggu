package com.zerobase.munbanggu.user.exception;


import com.zerobase.munbanggu.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserException extends RuntimeException{

  private ErrorCode errorCode;
  private String message;

  public UserException(ErrorCode errorCode) {
    this.errorCode = errorCode;
    this.message = errorCode.getDescription();
  }

}
