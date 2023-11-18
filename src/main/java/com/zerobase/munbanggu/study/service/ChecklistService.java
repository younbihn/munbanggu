package com.zerobase.munbanggu.study.service;

import com.zerobase.munbanggu.study.exception.StudyException;
import com.zerobase.munbanggu.study.model.entity.Checklist;
import com.zerobase.munbanggu.study.model.entity.Study;
import com.zerobase.munbanggu.study.repository.ChecklistRepository;
import com.zerobase.munbanggu.study.repository.StudyRepository;
import com.zerobase.munbanggu.study.type.AccessType;
import com.zerobase.munbanggu.type.ErrorCode;
import com.zerobase.munbanggu.user.exception.UserException;
import com.zerobase.munbanggu.user.repository.UserRepository;
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

  @Transactional
  public String createChecklist(Long userId, Long studyId, String title, AccessType ownerType){
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
    return "Added Successfully\n id: "+savedChecklist.getId()+" title: "+title ;
  }
  @Transactional
  public String editChecklist(Long userId, Long checklistId, String title){
    Checklist checklist = checklistRepository.findById(checklistId)
        .orElseThrow(() -> new StudyException(ErrorCode.CHECKLIST_NOT_EXIST));

    if (checklist != null && userId.equals(checklist.getUser_id())) {
      checklist.setTodo(title);
      checklistRepository.save(checklist);
    }
    else
      throw new UserException(ErrorCode.TOKEN_UNMATCHED);

    return "Updated Successfully\n id: "+checklist.getId()+" title: "+title;
  }

  @Transactional
  public String deleteChecklist(Long userId, Long checklistId){
    Checklist checklist = checklistRepository.findById(checklistId)
        .orElseThrow(() -> new StudyException(ErrorCode.CHECKLIST_NOT_EXIST));

    if (checklist != null && userId.equals(checklist.getUser_id()))
      checklistRepository.deleteById(checklistId);
    else
      throw new UserException(ErrorCode.TOKEN_UNMATCHED);

    return "Deleted Successfully\n id: "+checklist.getId();
  }

  @Transactional
  public String changeStatus(Long userId, Long checklistId, boolean status){
    Checklist checklist = checklistRepository.findById(checklistId)
        .orElseThrow(() -> new StudyException(ErrorCode.CHECKLIST_NOT_EXIST));

    boolean prev = checklist.isDone(); // 초기값

    if (checklist != null && userId.equals(checklist.getUser_id())) {
      checklist.setDone(status);
      checklistRepository.save(checklist);
    }else
      throw new UserException(ErrorCode.TOKEN_UNMATCHED);

    return "Status changed Successfully\n"+prev +" -> "+ checklist.isDone();
  }

  /**
   * 사용자의 개인 체크리스트 및 참여하고 있는 스터디의 체크리스트 조회
   * @param studyIds 사용자가 참여하고 있는 모든 스터디 ID list
   * @return Map<스터디 ID, List<Checklist>>
   */
  public Map<String,List<Checklist>> findAllMissions(List<Study> studyIds){

    // 사용자가 참여 중인 스터디의 체크리스트와 개인 체크리스트 목록 조회
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


  public List<Checklist> findStudyMissionList(Long studyId, Long userId){
    //스터디에서 사용자와 모임장이 생성한 체크리스트 조회
    Study study = studyRepository.findById(studyId)
        .orElseThrow(() -> new StudyException(ErrorCode.STUDY_NOT_EXIST));

    List<Checklist> userMissionList =
        checklistRepository.findByUserIdAndAccessType(userId, AccessType.USER);

    List<Checklist> studyMissionList =
        checklistRepository.findByStudyAndAccessType(study, AccessType.STUDY);

    return Stream.concat(studyMissionList.stream(), userMissionList.stream())
        .collect(Collectors.toList());

  }
}
