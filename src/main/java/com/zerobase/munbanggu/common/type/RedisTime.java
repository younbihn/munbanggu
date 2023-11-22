package com.zerobase.munbanggu.common.type;

import lombok.Getter;

@Getter
public enum RedisTime {
  MAIL_VALID(5),
  PHONE_VALID(2);

  private long time;

  private RedisTime(long time) {
    this.time = time;
  }
}
