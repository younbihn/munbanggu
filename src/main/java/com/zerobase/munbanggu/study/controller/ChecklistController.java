package com.zerobase.munbanggu.study.controller;

import com.zerobase.munbanggu.common.exception.InvalidTokenException;
import com.zerobase.munbanggu.common.util.ValidationService;
import com.zerobase.munbanggu.common.type.ErrorCode;
import com.zerobase.munbanggu.study.dto.ChangeStatusDto;
import com.zerobase.munbanggu.study.dto.CreateChecklistDto;
import com.zerobase.munbanggu.study.model.entity.Checklist;
import com.zerobase.munbanggu.study.model.entity.Study;
import com.zerobase.munbanggu.study.service.ChecklistService;
import com.zerobase.munbanggu.study.service.StudyService;
import com.zerobase.munbanggu.study.type.AccessType;
import com.zerobase.munbanggu.user.model.entity.User;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
public class ChecklistController {
    private static final String AUTH_HEADER = "Authorization";
    private final StudyService studyService;
    private final ChecklistService checkListService;
    private final ValidationService validationService;
  /**
   * 스터디 체크리스트 추가하기
   *
   * @param studyId 스터디ID
   * @param token   토큰
   * @return ResponseEntity<String> 리스트 추가 결과
   */
    @PostMapping("/{study_id}/mission")
    public ResponseEntity<String> createList(
          @PathVariable("study_id") Long studyId,
          @RequestHeader(name = AUTH_HEADER) String token,
          CreateChecklistDto createChecklistDto){

      User user = validationService.getUserFromToken(token);
      if(validationService.verifyUserNStudy(token,studyId)) {
        checkListService.createChecklist(user.getId(), studyId, createChecklistDto.getTitle(), AccessType.STUDY);
        return ResponseEntity.ok().body("체크리스트 생성 완료");
      }
      return ResponseEntity.badRequest().body("사용자 아이디와 토큰정보가 일치하지 않습니다 ");
    }

    /**
     * 개인 체크리스트 추가하기
     *
     * @param userId  사용자ID
     * @param studyId 스터디ID
     * @param token   토큰
     * @return ResponseEntity<String> 리스트 추가 결과
     */
    @PostMapping("/user/{user_id}/study/{study_id}/mission")
    public ResponseEntity<String> addPrivateList(
          @PathVariable("user_id") Long userId,
          @PathVariable("study_id") Long studyId,
          @RequestHeader("Authorization") String token,
          CreateChecklistDto createChecklistDto) {

      if (validationService.verifyUserToken(token, userId, studyId)) {
        checkListService.createChecklist(userId, studyId, createChecklistDto.getTitle(), AccessType.USER);
        return ResponseEntity.ok().body("체크리스트 생성 완료");
      }
      return ResponseEntity.badRequest().body("사용자 아이디와 토큰정보가 일치하지 않습니다 ");
    }

    /**
     * 체크리스트 수정하기
     *
     * @param studyId     스터디ID
     * @param checklistId 체크리스트 ID
     * @param token       토큰
     * @return ResponseEntity<String> 수정 결과 (성공/실패)
     */
    @PatchMapping("/{study_id}/mission/{mission_id}")
    public ResponseEntity<String> editList(
          @PathVariable("study_id") Long studyId,
          @PathVariable("mission_id") Long checklistId,
          @RequestHeader("Authorization") String token,
          CreateChecklistDto updateChecklistDto){

      if (validationService.verifyUserNStudy(token,studyId)) {
        User user = validationService.getUserFromToken(token);
        checkListService.editChecklist(user.getId(), checklistId, updateChecklistDto.getTitle());
        return ResponseEntity.ok().body("체크리스트 수정 완료");
      }
      return ResponseEntity.badRequest().body("사용자 아이디와 토큰정보가 일치하지 않습니다 ");
    }

    /**
     * 체크리스트 삭제 기능
     * 
     * @param studyId     스터디ID
     * @param checklistId 체크리스트 ID
     * @param token       토큰
     * @return ResponseEntity<String> 삭제 결과
     */
    @DeleteMapping("/{study_id}/mission/{mission_id}")
    public ResponseEntity<String> deleteList(
        @PathVariable("study_id") Long studyId,
        @PathVariable("mission_id") Long checklistId,
        @RequestHeader("Authorization") String token) {

      if(validationService.verifyUserNStudy(token,studyId)) {
        checkListService.deleteChecklist(validationService.getUserFromToken(token).getId() , checklistId);
        return ResponseEntity.ok().body("체크리스트 삭제 완료");
      }
      return ResponseEntity.badRequest().body("사용자 아이디와 토큰정보가 일치하지 않습니다 ");
    }

    /**
     * 체크리스트 상태 변경 (할일 완료 여부 체크)
     * 토큰 정보와 사용자 아이디, 체크리스트 생성자 아이디가 동일하면 상태변경 가능
     * @param studyId     스터디ID
     * @param userId      로그인한 사용자 ID
     * @param checklistId 체크리스트 ID
     * @param token       토큰
     * @return ResponseEntity<String> 변경 상태
     */
    @PatchMapping("/{study_id}/user/{user_id}/mission/{mission_id}")
    public ResponseEntity<String> changeStatus(
          @PathVariable("study_id") Long studyId,
          @PathVariable("user_id") Long userId,
          @PathVariable("mission_id") Long checklistId,
          @RequestHeader("Authorization") String token,
          ChangeStatusDto changeStatusDto) {

      validationService.verifyUserNListcreator(token,userId,studyId,checklistId);

      if ( validationService.verifyUserNListcreator(token,userId,studyId,checklistId)) {
        checkListService.changeStatus(userId, checklistId,
            changeStatusDto.isStatus());
        return ResponseEntity.ok().body("상태 변경 완료");
      }
      return ResponseEntity.badRequest().body("사용자 아이디와 토큰정보가 일치하지 않습니다 ");
    }

    /**
     * 사용자의 개인 체크리스트 및 사용자가 참여하고 있는 스터디에서 생성한 모든 체크리스트를
     * 스터디별로 그룹화하여 조회
     *
     * @param userId  사용자ID
     * @param token   토큰
     * @return ResponseEntity<Map<String, List<Checklist>>>
     * [스터디 아이디: [체크리스트1, 체크리스트2,,, ] ]
     */
    @GetMapping("/user/{user_id}/mission")
    public ResponseEntity<Map<String, List<Checklist>>> getPrivateMissionList(
        @PathVariable("user_id") Long userId,
        @RequestHeader("Authorization") String token) {

      User user = validationService.getUserFromToken(token);
      if (!userId.equals(user.getId()))
        throw new InvalidTokenException(ErrorCode.TOKEN_UNMATCHED);

      // 참여하고 있는 스터디 리스트
      List<Study> studyIds = studyService.findStudiesByUserId(userId);
      Map<String, List<Checklist>> lists = checkListService.findAllMissions(studyIds);
      return ResponseEntity.ok(lists);
    }

    /**
     * 스터디 체크리스트 조회 (스터디 내 개인 + 스터디 공통 체크리스트 출력)
     *
     * @param studyId 스터디ID
     * @param token   토큰
     * @return ResponseEntity<List<Checklist>> 체크리스트
     */
    @GetMapping("/{study_id}/mission")
    public ResponseEntity<List<Checklist>> getStudyMissionList(
          @PathVariable("study_id") Long studyId,
          @RequestHeader("Authorization") String token) {

      if ( validationService.verifyUserNStudy(token, studyId))
        // 스터디에서 사용자와 모임장이 생성한 체크리스트 조회
      {
        Long userId = validationService.getUserFromToken(token).getId();
        List<Checklist> checklists = checkListService.findStudyMissionList(studyId, userId);
        return ResponseEntity.ok(checklists);
      }
      else
        return ResponseEntity.ok(Collections.emptyList());
    }

  /**
   * 스터디의 미션 달성도 확인
   *
   * @param studyId 사용자ID
   * @return ResponseEntity<Double> 스터디원 전체 미션 달성도
   */
  @GetMapping("/{study_id}/participation")
  public ResponseEntity<Double> checkStudyProgress(@PathVariable ("study_id") Long studyId ){
    return ResponseEntity.ok(checkListService.getStudyParticipationRate(studyId));
  }

  /**
   * 유저 개인의 미션 달성도를 확인
   *
   * @param studyId   스터디ID
   * @param userId    사용자ID
   * @return ResponseEntity<Double> 멤버 개인의 달성도
   */
  @GetMapping("/{study_id}/participation/{user_id}")
  public ResponseEntity<Double> checkUserProgress(@PathVariable ("study_id") Long studyId , @PathVariable ("user_id") Long userId ){
    return ResponseEntity.ok(checkListService.getParticipationRate(studyId, userId));
  }
}
