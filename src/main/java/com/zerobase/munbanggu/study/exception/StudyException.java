package com.zerobase.munbanggu.study.exception;


import com.zerobase.munbanggu.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudyException extends RuntimeException{

  private ErrorCode errorCode;
  private String message;

  public StudyException(ErrorCode errorCode) {
    this.errorCode = errorCode;
    this.message = errorCode.getDescription();
  }

}
