package com.zerobase.munbanggu.study.service;

import com.zerobase.munbanggu.study.exception.StudyException;
import com.zerobase.munbanggu.study.model.entity.Checklist;
import com.zerobase.munbanggu.study.model.entity.Study;
import com.zerobase.munbanggu.study.repository.ChecklistRepository;
import com.zerobase.munbanggu.study.repository.StudyRepository;
import com.zerobase.munbanggu.study.type.AccessType;
import com.zerobase.munbanggu.type.ErrorCode;
import com.zerobase.munbanggu.user.exception.UserException;
import com.zerobase.munbanggu.user.model.entity.StudyUser;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.repository.StudyUserRepository;
import com.zerobase.munbanggu.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChecklistService {

  @Autowired
  private ChecklistRepository checklistRepository;

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private StudyUserRepository studyUserRepository;

  @Transactional
  public String createChecklist(Long userId, Long studyId, String title, AccessType ownerType) {
    Study study = studyRepository.findById(studyId)
        .orElseThrow(() -> new StudyException(ErrorCode.STUDY_NOT_EXIST));

    Checklist checklist = Checklist.builder()
        .user_id(userId)
        .todo(title)
        .done(false)
        .accessType(ownerType)
        .build();
    checklist.setStudy(study);

    studyRepository.save(study);
    Checklist savedChecklist = checklistRepository.save(checklist);
    return "Added Successfully\n id: " + savedChecklist.getId() + " title: " + title;
  }

  @Transactional
  public String editChecklist(Long userId, Long checklistId, String title) {
    Checklist checklist = checklistRepository.findById(checklistId)
        .orElseThrow(() -> new StudyException(ErrorCode.CHECKLIST_NOT_EXIST));

    if (checklist != null && userId.equals(checklist.getUser_id())) {
      checklist.setTodo(title);
      checklistRepository.save(checklist);
    } else
      throw new UserException(ErrorCode.TOKEN_UNMATCHED);

    return "Updated Successfully\n id: " + checklist.getId() + " title: " + title;
  }

  @Transactional
  public String deleteChecklist(Long userId, Long checklistId) {
    Checklist checklist = checklistRepository.findById(checklistId)
        .orElseThrow(() -> new StudyException(ErrorCode.CHECKLIST_NOT_EXIST));

    if (checklist != null && userId.equals(checklist.getUser_id()))
      checklistRepository.deleteById(checklistId);
    else
      throw new UserException(ErrorCode.TOKEN_UNMATCHED);

    return "Deleted Successfully\n id: " + checklist.getId();
  }

  @Transactional
  public String changeStatus(Long userId, Long checklistId, boolean status) {
    Checklist checklist = checklistRepository.findById(checklistId)
        .orElseThrow(() -> new StudyException(ErrorCode.CHECKLIST_NOT_EXIST));

    boolean prev = checklist.isDone(); // 초기값

    if (checklist != null && userId.equals(checklist.getUser_id())) {
      checklist.setDone(status);
      checklistRepository.save(checklist);
    } else
      throw new UserException(ErrorCode.TOKEN_UNMATCHED);

    return "Status changed Successfully\n" + prev + " -> " + checklist.isDone();
  }

  /**
   * 사용자의 개인 체크리스트 및 참여하고 있는 스터디의 체크리스트 조회
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
                    .orElseThrow(() -> new StudyException(ErrorCode.STUDY_NOT_EXIST))
                    .getTitle(), Collectors.toList()
            )
        );
  }

  public List<Checklist> findStudyMissionList(Long studyId, Long userId) {
    Study study = studyRepository.findById(studyId)
        .orElseThrow(() -> new StudyException(ErrorCode.STUDY_NOT_EXIST));

    List<Checklist> userMissionList =
        checklistRepository.findByUserIdAndAccessType(userId, AccessType.USER);

    List<Checklist> studyMissionList =
        checklistRepository.findByStudyAndAccessType(study, AccessType.STUDY);

    return Stream.concat(studyMissionList.stream(), userMissionList.stream())
        .collect(Collectors.toList());

  }

  // 개인 달성도
  public double getParticipationRate(Long userId, Long studyId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_EXIST));
    Study study = studyRepository.findById(studyId)
        .orElseThrow(() -> new StudyException(ErrorCode.STUDY_NOT_EXIST));

    if (user != null && study != null) {
      StudyUser studyUser = studyUserRepository.findByUserAndStudy(user, study);
      int cycle = study.getRefundCycle().getValue();

      if (studyUser != null) {
        return calculateParticipationRate(studyUser.getChecklists(),
            studyUser.getLatestCertificationDate().plusMonths(cycle));
      }
    }
    throw new StudyException(ErrorCode.INVALID_USER_OR_STUDY);
  }

  // 스터디에 참여하는 유저들의 평균 달성도
  public Double getStudyParticipationRate(Long studyId) {
    Study study = studyRepository.findById(studyId)
        .orElseThrow(() -> new StudyException(ErrorCode.STUDY_NOT_EXIST));

    if (study != null) {
      List<StudyUser> participants = studyUserRepository.findByStudy(study);
      int participantNum = participants.size();
      int cycle = study.getRefundCycle().getValue();
      double total = 0.0;

      if (participantNum == 0)
        throw new StudyException(ErrorCode.INSUFFICIENT_USER_CAPACITY);

      for (StudyUser studyUser : participants) {
        total += calculateParticipationRate(studyUser.getChecklists(),
            studyUser.getLatestCertificationDate().plusMonths(cycle));
      }
      return total / participantNum;
    }
    throw new StudyException(ErrorCode.STUDY_NOT_EXIST);
  }

  public double calculateParticipationRate(List<Checklist> checklists,LocalDateTime endDate) {
    int checklistNum = checklists.size();

    if (checklistNum == 0)
      return 0.0;

    long completedNum = checklists.stream()
        .filter(checklist -> checklist.isDone()
        && checklist.getCreatedDate().isBefore(endDate))
        .count();

    return (completedNum / checklistNum) * 100.0;
  }
}