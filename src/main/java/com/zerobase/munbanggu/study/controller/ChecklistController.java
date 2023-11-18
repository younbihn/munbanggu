package com.zerobase.munbanggu.study.controller;

import com.zerobase.munbanggu.study.model.entity.Checklist;
import com.zerobase.munbanggu.study.model.entity.Study;
import com.zerobase.munbanggu.study.service.ChecklistService;
import com.zerobase.munbanggu.study.service.StudyService;
import com.zerobase.munbanggu.study.type.AccessType;
import com.zerobase.munbanggu.type.ErrorCode;
import com.zerobase.munbanggu.user.exception.UserException;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.service.UserService;
import com.zerobase.munbanggu.util.JwtService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
public class ChecklistController {
    private static final String AUTH_HEADER = "Authorization";
    private final StudyService studyService;
    private final UserService userService;
    private final JwtService jwtService;
  private final ChecklistService checkListService;

  /**
     * 토큰과 스터디 아이디 검증
     * @param token 토큰
     * @param studyId 스터디ID
     * @return 토큰과 스터디 아이디가 유효하면 user, 불일치시 null 반환
     */
    public User verifyUserNStudy(String token, Long studyId){

      User user = userService.getUser(jwtService.getIdFromToken(token))
          .orElseThrow(() -> new UserException(ErrorCode.INVALID_TOKEN));
      Study study = studyService.getStudy(studyId);
      if (user != null && study!= null)
        return user;
      else
        return null;
    }

    /**
     * 요청하는 사용자와 로그인한 사용자의 아이디 일치 여부 반환
     * @param token 토큰
     * @param userId 사용자ID
     * @param studyId 스터디ID
     * @return userId와 token.getId()의 일치시 user , 불일치시 null 반환
     */
    public User verifyUserToken(String token, Long userId, Long studyId){

      User userFromToken = verifyUserNStudy(token, studyId);
      if( userFromToken != null )
        return userService.findByIdAndToken(userFromToken.getId(), userId);
      return null;
    }

    /**
     * 스터디 체크리스트 추가하기
     * @param studyId 스터디ID
     * @param title 체크리스트 내용
     * @param token 토큰
     * @return 리스트 추가 결과 (성공/실패)
     */
    @PostMapping("/{study_id}/mission")
    public ResponseEntity<String> createList(
        @PathVariable("study_id") Long studyId,
        @RequestParam String title,
        @RequestHeader(name = AUTH_HEADER) String token) {

      User user = verifyUserNStudy(token,studyId);
      if (user != null)
        return ResponseEntity.ok(
            checkListService.createChecklist(user.getId(), studyId, title, AccessType.STUDY));
      return ResponseEntity.ok("Adding study list Failed");
    }

    /**
     * 개인 체크리스트 추가하기
     * @param userId 사용자ID
     * @param studyId 스터디ID
     * @param title 체크리스트 내용
     * @param token 토큰
     * @return 리스트 추가 결과 (성공/실패)
     */
    @PostMapping("/user/{user_id}/study/{study_id}/mission")
    public ResponseEntity<String> addPrivateList(
        @PathVariable("user_id") Long userId,
        @PathVariable("study_id") Long studyId,
        @RequestParam String title,
        @RequestHeader("Authorization") String token) {

      if ( verifyUserToken(token, userId, studyId) != null)
        return ResponseEntity.ok(
            checkListService.createChecklist(userId, studyId, title, AccessType.USER));
      return ResponseEntity.ok("Adding private list Failed");
    }

    /**
     * 체크리스트 수정하기
     * @param studyId 스터디ID
     * @param checklistId 체크리스트 ID
     * @param title 체크리스트 내용
     * @param token 토큰
     * @return 수정 결과 (성공/실패)
     */
    @PatchMapping("/{study_id}/mission/{mission_id}")
    public ResponseEntity<String> editList(@PathVariable("study_id") Long studyId,
        @PathVariable("mission_id") Long checklistId,
        @RequestParam String title,
        @RequestHeader("Authorization") String token) {

      User user = verifyUserNStudy(token,studyId);
      if(user != null)
        return ResponseEntity.ok(checkListService.editChecklist(user.getId(), checklistId, title));
      return ResponseEntity.ok("Edit Failed");
    }

    /**
     * 체크리스트 삭제 기능
     * @param studyId 스터디ID
     * @param checklistId 체크리스트 ID
     * @param token 토큰
     * @return 삭제 결과 (성공/실패)
     */
    @DeleteMapping("/{study_id}/mission/{mission_id}")
    public ResponseEntity<String> deleteList(
        @PathVariable("study_id") Long studyId,
        @PathVariable("mission_id") Long checklistId,
        @RequestHeader("Authorization") String token) {

      User user = verifyUserNStudy(token,studyId);
      if(user != null)
        return ResponseEntity.ok(checkListService.deleteChecklist(user.getId(), checklistId));
      return ResponseEntity.ok("Delete Failed");
    }

    /**
     * 체크리스트 상태 변경 (할일 완료 여부 체크)
     * @param studyId 스터디ID
     * @param userId  로그인한 사용자 ID
     * @param checklistId 체크리스트 ID
     * @param status 체크리스트 상태 (완료/미완료)
     * @param token 토큰
     * @return 변경 상태 (성공/실패)
     */
    @PatchMapping("/{study_id}/user/{user_id}/mission/{mission_id}")
    public ResponseEntity<String> changeStatus(
        @PathVariable("study_id") Long studyId,
        @PathVariable("user_id") Long userId,
        @PathVariable("mission_id") Long checklistId,
        @RequestParam boolean status,
        @RequestHeader("Authorization") String token) {

          /*  if (token.getId() == (@PathVariable) userId == checklist.getUserId ) -> 상태변경
              else -> 하나라도 틀리면 변경 x */
      if ( verifyUserToken(token, userId, studyId) != null)
        return ResponseEntity.ok(checkListService.changeStatus(userId, checklistId,status));
      return ResponseEntity.ok("Changing status Failed");
    }

    /**
     * 사용자의 개인 체크리스트 및 사용자가 참여하고 있는 스터디에서 생성한 모든 체크리스트를
     * 스터디별로 그룹화하여 조회
     * @param userId 사용자ID
     * @param token 토큰
     * @return [스터디 아이디: [체크리스트1, 체크리스트2,,, ] ]
     */
    @GetMapping("/user/{user_id}/mission")
    public ResponseEntity<Map<String, List<Checklist>>> getPrivateMissionList(
        @PathVariable("user_id") Long userId,
        @RequestHeader("Authorization") String token) {

      User user = userService.getUser(jwtService.getIdFromToken(token))
          .orElseThrow(() -> new UserException(ErrorCode.INVALID_TOKEN));

      if (!userId.equals(user.getId()))
        throw new UserException(ErrorCode.TOKEN_UNMATCHED);

      // 참여하고 있는 스터디 리스트
      List<Study> studyIds = studyService.findStudiesByUserId(userId);
      return ResponseEntity.ok(checkListService.findAllMissions(studyIds));

    }

    /**
     * 스터디 체크리스트 조회 (스터디 내 개인 + 스터디 공통 체크리스트 출력)
     * @param studyId 스터디ID
     * @param token 토큰
     * @return 체크리스트
     */
    @GetMapping("/{study_id}/mission")
    public ResponseEntity<List<Checklist>> getStudyMissionList(
        @PathVariable("study_id") Long studyId,
        @RequestHeader("Authorization") String token) {

      User user = verifyUserNStudy(token, studyId);

      if (user != null)
        // 스터디에서 사용자와 모임장이 생성한 체크리스트 조회
        return ResponseEntity.ok(checkListService.findStudyMissionList(studyId, user.getId()));
      else
        return ResponseEntity.ok(Collections.emptyList());
    }
}
