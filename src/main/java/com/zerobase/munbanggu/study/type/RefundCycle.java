package com.zerobase.munbanggu.study.type;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RefundCycle {
  ONE(1), TWO(2), THREE(3),
  FOUR(4), FIVE(5),SIX(6);

  private final int value;

  public int getValue(){
    return value;
  }
}
