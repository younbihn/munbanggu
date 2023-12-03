package com.zerobase.munbanggu.study.service;

import com.zerobase.munbanggu.common.exception.InsufficientUserCapacityException;
import com.zerobase.munbanggu.common.exception.InvalidTokenException;
import com.zerobase.munbanggu.common.exception.NotFoundChecklistException;
import com.zerobase.munbanggu.common.exception.NotFoundStudyException;
import com.zerobase.munbanggu.common.exception.NotFoundUserException;
import com.zerobase.munbanggu.study.model.entity.Checklist;
import com.zerobase.munbanggu.study.model.entity.Study;
import com.zerobase.munbanggu.study.repository.ChecklistRepository;
import com.zerobase.munbanggu.study.repository.StudyRepository;
import com.zerobase.munbanggu.study.type.AccessType;
import com.zerobase.munbanggu.study.type.ChecklistCycle;
import com.zerobase.munbanggu.common.type.ErrorCode;
import com.zerobase.munbanggu.user.model.entity.StudyUser;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.repository.StudyUserRepository;
import com.zerobase.munbanggu.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChecklistService {
  private final ChecklistRepository checklistRepository;
  private final StudyRepository studyRepository;
  private final UserRepository userRepository;
  private final StudyUserRepository studyUserRepository;

  /**
   * 체크리스트 생성
   *
   * @param userId  사용자ID
   * @param studyId 스터디ID
   * @param title   체크리스트 내용
   * @param ownerType 체크리스트 타입 (스터디/ 개인)
   */
  @Transactional
  public void createChecklist(Long userId, Long studyId, String title, AccessType ownerType) {
    Study study = studyRepository.findById(studyId)
        .orElseThrow(() -> new NotFoundStudyException(ErrorCode.STUDY_NOT_EXIST));

    Checklist checklist = Checklist.builder()
        .userId(userId)
        .todo(title)
        .done(false)
        .accessType(ownerType)
        .build();
    checklist.setStudy(study);
    checklistRepository.save(checklist);
  }

  /**
   * 체크리스트 수정
   *
   * @param userId      사용자ID
   * @param checklistId 체크리스트ID
   * @param title       체크리스트 내용
   */
  @Transactional
  public void editChecklist(Long userId, Long checklistId, String title) {
    Checklist checklist = checklistRepository.findById(checklistId)
        .orElseThrow(() -> new NotFoundChecklistException(ErrorCode.CHECKLIST_NOT_EXIST));

    if (userId.equals(checklist.getUserId())) {
      checklist.setTodo(title);
      checklistRepository.save(checklist);
    } else
      throw new InvalidTokenException(ErrorCode.TOKEN_UNMATCHED);
  }

  /**
   * 체크리스트 삭제
   *
   * @param userId      사용자ID
   * @param checklistId 체크리스트ID
   */
  @Transactional
  public void deleteChecklist(Long userId, Long checklistId) {
    Checklist checklist = checklistRepository.findById(checklistId)
        .orElseThrow(() -> new NotFoundChecklistException(ErrorCode.CHECKLIST_NOT_EXIST));

    if (userId.equals(checklist.getUserId()))
      checklistRepository.deleteById(checklistId);
    else
      throw new InvalidTokenException(ErrorCode.TOKEN_UNMATCHED);
  }

  /**
   * 체크리스트 상태 변경
   *
   * @param userId      사용자ID
   * @param checklistId 체크리스트ID
   * @param status      완료상태
   */
  @Transactional
  public void changeStatus(Long userId, Long checklistId, boolean status) {
    Checklist checklist = checklistRepository.findById(checklistId)
        .orElseThrow(() -> new NotFoundChecklistException(ErrorCode.CHECKLIST_NOT_EXIST));

    if (userId.equals(checklist.getUserId())) {
      checklist.setDone(status);
      checklistRepository.save(checklist);
    } else
      throw new InvalidTokenException(ErrorCode.TOKEN_UNMATCHED);
  }

  /**
   * 사용자의 개인 체크리스트 및 참여하고 있는 스터디의 체크리스트 조회
   *
   * @param studyIds 사용자가 참여하고 있는 모든 스터디 ID list
   * @return Map<스터디 ID, List < Checklist>>
   */
  public Map<String, List<Checklist>> findAllMissions(List<Study> studyIds) {

    List<Checklist> checklists = checklistRepository.findByStudyIn(studyIds);

    // 스터디 그룹별로 체크리스트 그룹화
    return checklists.stream()
        .collect(
            // 스터디 아이디로 그룹화
            Collectors.groupingBy(
                checklist -> studyRepository.findById(checklist.getStudy().getId())
                    .orElseThrow(() -> new NotFoundStudyException(ErrorCode.STUDY_NOT_EXIST))
                    .getTitle(), Collectors.toList()
            )
        );
  }

  /**
   * 스터디에서 사용자와 모임장이 생성한 체크리스트 조회
   *
   * @param studyId 스터디ID
   * @param userId  사용자ID
   * @return List<Checklist> 체크리스트목록
   */
  public List<Checklist> findStudyMissionList(Long studyId, Long userId) {
    Study study = studyRepository.findById(studyId)
        .orElseThrow(() -> new NotFoundStudyException(ErrorCode.STUDY_NOT_EXIST));

    List<Checklist> userMissionList =
        checklistRepository.findByUserIdAndAccessType(userId, AccessType.USER);

    List<Checklist> studyMissionList =
        checklistRepository.findByStudyAndAccessType(study, AccessType.STUDY);

    return Stream.concat(studyMissionList.stream(), userMissionList.stream())
        .collect(Collectors.toList());

  }

  /**
   * 개인 달성도 구하기
   *
   * @param userId  사용자ID
   * @param studyId 스터디ID
   * @return double 개인 달성도
   */
  public double getParticipationRate(Long userId, Long studyId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));
    Study study = studyRepository.findById(studyId)
        .orElseThrow(() -> new NotFoundStudyException(ErrorCode.STUDY_NOT_EXIST));

    StudyUser studyUser = studyUserRepository.findByUserAndStudy(user, study);
    return studyUser.getParticipationRate();

  }

  /**
   * 스터디에 참여하는 유저들의 평균 달성도
   * @param studyId 스터디ID
   * @return double 평균 달성도
   */
  public Double getStudyParticipationRate(Long studyId) {
    Study study = studyRepository.findById(studyId)
        .orElseThrow(() -> new NotFoundStudyException(ErrorCode.STUDY_NOT_EXIST));

    List<StudyUser> participants = studyUserRepository.findByStudy(study);
    int participantNum = participants.size();
    double total = 0.0;

    if (participantNum == 0)
      throw new InsufficientUserCapacityException(ErrorCode.INSUFFICIENT_USER_CAPACITY);

    for (StudyUser studyUser : participants) {
      total += studyUser.getParticipationRate();
    }
    return total / participantNum;
  }

  /**
   * 유저 참여도 매일 자정에 업데이트 실행
   * 인증사이클이 매일이면 interval=1, 일주일에 n회면 interval=7일마다 업데이트
   *
   * @param studyUser 유저의 스터디 가입정보
   * @param study     스터디
   */
  @Scheduled(cron = "0 0 0 * * ?")  //매일 자정에 실행
  //TODO: 스케줄러에 옮기기
  public void updateParticipationRate(StudyUser studyUser, Study study) {
    ChecklistCycle cycle = study.getChecklist_cycle();
    LocalDateTime lastCertificationDate = study.getLatest_refund_date();

    int interval = cycle.isDaily() ? 1 : 7 ;
    updateRate(studyUser,lastCertificationDate,interval);
  }

  /**
   * 참여도 정보를 업데이트
   *
   * @param studyUser       유저의 스터디 가입정보
   * @param lastRefundDate  최근 환급일
   * @param interval        인증주기
   */
  public void updateRate(StudyUser studyUser, LocalDateTime lastRefundDate, int interval){
    long daysBetween = ChronoUnit.DAYS.between(lastRefundDate.toLocalDate(), LocalDateTime.now().toLocalDate());
    if (daysBetween >= interval) {
      double participationRate = calculateParticipationRate(studyUser.getChecklists(),
                  studyUser.getStudy().getLatest_refund_date().plusDays(interval).toLocalDate());

      studyUser.setParticipationRate(participationRate);
      studyUser.getStudy().setLatest_refund_date(LocalDateTime.now());
      studyUserRepository.save(studyUser);
    }
  }

  public double calculateParticipationRate(List<Checklist> checklists, LocalDate endDate) {
    int checklistNum = checklists.size();

    if (checklistNum == 0)
      return 0.0;

    long completedNum = checklists.stream()
        .filter(checklist -> checklist.isDone()
            && checklist.getCreatedDate().toLocalDate().isBefore(endDate))
        .count();

    return ((double) completedNum / checklistNum) * 100.0;
  }
}