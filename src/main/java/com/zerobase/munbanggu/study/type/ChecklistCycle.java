package com.zerobase.munbanggu.study.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ChecklistCycle {
  EVERY_WEEKDAY("평일 매일"),
  EVERY_WEEKEND("주말 매일"),
  MON_TO_SAT("월-토 매일"),
  ONCE_A_WEEK("주 1일"),
  TWICE_A_WEEK("주 2일"),
  THIRD_A_WEEK("주 3일");

  private final String desc;

  public String getCycleStr() {
    return desc;
  }

  public boolean isDaily() {
    List<ChecklistCycle> lst = new ArrayList<>(Arrays.asList(EVERY_WEEKDAY,EVERY_WEEKEND,MON_TO_SAT));
    return lst.contains(this);
  }

  public boolean isWeekly() {
    List<ChecklistCycle> lst = new ArrayList<>(Arrays.asList(ONCE_A_WEEK,TWICE_A_WEEK,THIRD_A_WEEK));
    return lst.contains(this);
  }

}
