package com.zerobase.munbanggu.point.service;

import com.zerobase.munbanggu.point.model.Point;
import com.zerobase.munbanggu.point.repository.PointRepository;
import com.zerobase.munbanggu.point.type.UsageType;
import com.zerobase.munbanggu.study.model.entity.Checklist;
import com.zerobase.munbanggu.study.model.entity.Study;
import com.zerobase.munbanggu.study.repository.StudyRepository;
import com.zerobase.munbanggu.study.type.RefundCycle;
import com.zerobase.munbanggu.user.model.entity.StudyUser;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.repository.StudyUserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

  private final StudyUserRepository studyUserRepository;
  private final PointRepository pointRepository;
  private final StudyRepository studyRepository;
  @Scheduled(cron = "0 0 0 * * ?")
  //TODO : Scheled는 스케줄 파일 생성해서 추가하기 (위치 옮기기)
  public boolean getRefund(){
    List<Study> studyList = studyRepository.findAll();
    for(Study study : studyList){
      if (study.getLatest_refund_date().plusMonths(study.getRefundCycle().getValue()).isBefore(LocalDateTime.now()))
      {
        List<StudyUser> allStudyUsers = studyUserRepository.findByStudy(study);
        for(StudyUser studyUsers : allStudyUsers) {
          processRefund(studyUsers);
        }
      }
      return true;
    }
    return false;
  }

  /**
   * 포인트 환급 로직 처리
   *
   * @param studyUser 유저의 스터디 가입정보
   */
  private void processRefund(StudyUser studyUser){
    Study study = studyUser.getStudy();
    User user = studyUser.getUser();

    int incompleteListAmount = getIncompleteListAmount(studyUser);
    long deductionAmount = getDeductionAmount(studyUser, incompleteListAmount);

    if (incompleteListAmount > 0){
      // 포인트 차감
      refundPoint(user,study,deductionAmount,UsageType.DEDUCTION);
    }
    else{
      Point latestRecord = pointRepository.findTopByUserByCreatedDateDesc(user);
      long additionalRefund = getAdditionalRefundAmount(studyUser);
      long totalRefund = latestRecord.getBalance() + additionalRefund;

      refundPoint(user,study,totalRefund,UsageType.REFUND);
    }
    // 환급일 갱신
    study.setLatest_refund_date(LocalDateTime.now());
    studyRepository.save(study);
  }

  /**
   * 유저 개개인 환급
   *
   * @param currentUser   유저
   * @param currentStudy  스터디
   */
  public void getUserRefund(User currentUser, Study currentStudy){
    StudyUser currentStudyUser = studyUserRepository.findByUserAndStudy(currentUser,currentStudy);
    RefundCycle refundCycle = currentStudy.getRefundCycle();
    LocalDateTime latestRefundDate = currentStudy.getLatest_refund_date();

    // 환급 가능하면
    if ( latestRefundDate.plusMonths(refundCycle.getValue()).isBefore(LocalDateTime.now()) ) {
      processRefund(currentStudyUser);
    }
  }

  /**
   * 포인트 환급 정보 저장
   *
   * @param user      사용자
   * @param study     스터디
   * @param Amount    환급금액
   * @param usageType 환급종류
   */
  private void refundPoint(User user, Study study, long Amount, UsageType usageType) {
    Point latestRecord = pointRepository.findTopByUserByCreatedDateDesc(user);
    Amount *= usageType.equals(UsageType.DEDUCTION) ? -1 : 1;
    Point.builder()
        .type(usageType)
        .study(study)
        .user(user)
        .amount(Amount)
        .balance(latestRecord.getBalance() + Amount)
        .createdDate(LocalDateTime.now())
        .build();
  }

  /**
   * 체크리스트를 완료하지 못했을 때 차감될 금액 산출.
   *
   * @param studyUser   유저의 스터디 가입정보
   * @param incompleteChecklistNum 미완성 체크리스트 수
   * @return 공제할 금액 반환
   */
  private long getDeductionAmount(StudyUser studyUser, int incompleteChecklistNum) {
    Study study = studyUser.getStudy();
    long participationFee = study.getFee();
    int refundCycle = study.getRefundCycle().getValue();
    int checklistCycle = study.getChecklist_cycle().isDaily() ? 1 : 7;

    return (long) Math.ceil((double) participationFee / ((long) refundCycle * checklistCycle * incompleteChecklistNum));
  }

  /**
   * 100% 완료자 추가 환급금 계산
   * @param currentStudyUser 유저의 스터디 가입정보
   * @return long 환급금액 반환 - 스터디 참여비 + (미완료자 환급금 총합 * (1-deductionRate) / 완료자 수 )
   */
  private long getAdditionalRefundAmount(StudyUser currentStudyUser) {
    final double DEDUCTION_RATE = 0.1; //수수료(공제비율) : 10%
    long totalDeductionAmount = 0L;

    List<StudyUser> allUserStudies = studyUserRepository.findByStudy(currentStudyUser.getStudy());
    int count = 0 ;
    for (StudyUser studyUser : allUserStudies) {
      // 현재 사용자 x & complete 한 경우
      if (!studyUser.equals(currentStudyUser) && getIncompleteListAmount(studyUser) == 0) {
        count += 1;
        totalDeductionAmount += getDeductionAmount(studyUser, getIncompleteListAmount(studyUser));
      }
    }
    return (long) Math.floor((1 - DEDUCTION_RATE) * totalDeductionAmount / count);
  }

  /**
   * 미완료 체크리스트 개수 반환
   *
   * @param studyUser 유저의 스터디 가입정보
   * @return int
   */
  private int getIncompleteListAmount(StudyUser studyUser) {
    List<Checklist> checklists = studyUser.getChecklists();
    return Long.valueOf(checklists.stream().filter(checklist -> !checklist.isDone()).count()).intValue();
  }
}