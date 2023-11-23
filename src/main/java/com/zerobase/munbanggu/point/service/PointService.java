package com.zerobase.munbanggu.point.service;

import com.zerobase.munbanggu.point.model.Point;
import com.zerobase.munbanggu.point.repository.PointRepository;
import com.zerobase.munbanggu.point.type.UsageType;
import com.zerobase.munbanggu.study.exception.StudyException;
import com.zerobase.munbanggu.study.model.entity.Checklist;
import com.zerobase.munbanggu.study.model.entity.Study;
import com.zerobase.munbanggu.study.repository.StudyRepository;
import com.zerobase.munbanggu.study.type.RefundCycle;
import com.zerobase.munbanggu.common.type.ErrorCode;
import com.zerobase.munbanggu.user.exception.UserException;
import com.zerobase.munbanggu.user.model.entity.StudyUser;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.repository.StudyUserRepository;
import com.zerobase.munbanggu.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class PointService {
  private final double DEDUCTION_RATE = 0.1;  //수수료(공제비율) : 10%

  @Autowired
  private StudyUserRepository studyUserRepository;

  @Autowired
  private PointRepository pointRepository;

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private UserRepository userRepository;


  @Scheduled(cron = "0 0 0 * * ?")
  public String getRefund(){
    // 전체 유저 환금해야하느지 유무 파악
    List<Study> studyList = studyRepository.findAll();
    for(Study study : studyList){
      if (study.getLatest_refund_date().plusMonths(study.getRefundCycle().getValue()).isBefore(LocalDateTime.now()))
      {
        List<StudyUser> allStudyUsers = studyUserRepository.findByStudy(study);
        for(StudyUser studyUsers : allStudyUsers) {
          processRefund(studyUsers);
        }
      }
      return "환급 완료 ";
    }
    return "환급 불가능 ";
  }

  public void processRefund(StudyUser studyUser){
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

  // 유저 개개인의 환급 진행
  public String getUserRefund(Long userId, Long studyId){
    User currentUser = userRepository.findById(userId).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_EXIST));
    Study currentStudy = studyRepository.findById(studyId).orElseThrow(() -> new StudyException(ErrorCode.STUDY_NOT_EXIST));
    StudyUser currentStudyUser = studyUserRepository.findByUserAndStudy(currentUser,currentStudy);

    RefundCycle refundCycle = currentStudy.getRefundCycle();
    LocalDateTime latestRefundDate = currentStudy.getLatest_refund_date();

    // 환급 가능하면
    if ( latestRefundDate.plusMonths(refundCycle.getValue()).isBefore(LocalDateTime.now()) ) {
      processRefund(currentStudyUser);
      return "환급 완료 ";
    }
    return "환급일이 아닙니다 ";
  }

  /**
   * 포인트 환급 정보 저장
   * @param user
   * @param study
   * @param Amount
   * @param usageType
   */
  private void refundPoint(User user , Study study, long Amount, UsageType usageType){
    Point latestRecord = pointRepository.findTopByUserByCreatedDateDesc(user);

    Amount *= usageType.equals(UsageType.DEDUCTION) ? -1 : 1;

    Point.builder()
        .type(usageType)
        .study(study)
        .user(user)
        .amount(Amount)
        .balance(latestRecord.getBalance()+Amount)
        .createdDate(LocalDateTime.now())
        .build();
  }

  /**
   * 체크리스트를 완료하지 못했을 때 차감될 금액 산출.
   * @param studyUser
   * @param incompleteChecklistNum - 미완성 체크리스트 수
   * @return 공제할 금액 반환
   */
  private long getDeductionAmount(StudyUser studyUser, int incompleteChecklistNum) {
    Study study = studyUser.getStudy();
    long participationFee = study.getFee();
    int refundCycle = study.getRefundCycle().getValue();
    int checklistCycle = study.getChecklist_cycle().isDaily()? 1 : 7;

    return (long) Math.ceil(participationFee / (refundCycle * checklistCycle * incompleteChecklistNum));
  }

  /**
   * 100% 완료자 추가 환급금 계산
   * @param currentStudyUser
   * @return 환급금액 반환 - 스터디 참여비 + (미완료자 환급금 총합 * (1-deductionRate) / 완료자 수 )
   */
  private long getAdditionalRefundAmount(StudyUser currentStudyUser) {
    long totalDeductionAmount = 0L;
    List<StudyUser> allUserStudies = studyUserRepository.findByStudy(currentStudyUser.getStudy());
//    List<StudyUser> completeUser = new ArrayList<>();
    int count = 0 ;

    for (StudyUser studyUser : allUserStudies) {
      // 현재 사용자 x & complete 한 경우
      if (!studyUser.equals(currentStudyUser)&& getIncompleteListAmount(studyUser)==0) {
        count +=1;
        totalDeductionAmount += getDeductionAmount(studyUser, getIncompleteListAmount(studyUser));
      }
    }
    return (long) Math.floor((1-DEDUCTION_RATE) * totalDeductionAmount / count );
  }

  //미완료 체크리스트 개수 반환
  private int getIncompleteListAmount(StudyUser studyUser) {
    List<Checklist> checklists = studyUser.getChecklists();
    return Long.valueOf(checklists.stream().filter(checklist -> !checklist.isDone()).count()).intValue();
  }
}