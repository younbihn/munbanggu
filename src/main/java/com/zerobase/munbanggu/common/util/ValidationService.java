package com.zerobase.munbanggu.common.util;

import com.zerobase.munbanggu.auth.TokenProvider;
import com.zerobase.munbanggu.common.exception.NotFoundChecklistException;
import com.zerobase.munbanggu.common.exception.NotFoundStudyException;
import com.zerobase.munbanggu.common.exception.NotFoundUserException;
import com.zerobase.munbanggu.common.type.ErrorCode;
import com.zerobase.munbanggu.study.model.entity.Checklist;
import com.zerobase.munbanggu.study.repository.ChecklistRepository;
import com.zerobase.munbanggu.study.service.StudyService;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidationService {
  private final UserService userService;
  private final StudyService studyService;
  private final TokenProvider tokenProvider;
  private final ChecklistRepository checklistRepository;
  /**
   * 토큰에서 유저 정보 추출하기
   *
   * @param token 토큰
   * @return user
   */

  public User getUserFromToken(String token){
    User user = userService.getUser(tokenProvider.getId(token))
        .orElseThrow(()-> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));
    if (user == null)
      throw new NotFoundUserException(ErrorCode.USER_NOT_EXIST);
    return user;
  }

  /**
   * 토큰과 스터디 아이디 유효성검증
   *
   * @param token   토큰
   * @param studyId 스터디ID
   * @return 토큰과 스터디 아이디가 유효하면 true, 아니면 false 반환
   */
  public boolean verifyUserNStudy(String token, Long studyId){
    getUserFromToken(token);
    if (studyService.getStudy(studyId) == null)
      throw new NotFoundStudyException(ErrorCode.STUDY_NOT_EXIST);
    return true;
  }

  /**
   * 요청하는 사용자와 체크리스트 생성자의 일치여부 반환
   *
   * @param token         토큰
   * @param userId        사용자ID
   * @param studyId       스터디ID
   * @param checklistId   체크리스트ID
   * @return 일치여부
   */
  public boolean verifyUserNListcreator(String token,Long userId,Long studyId,Long checklistId) {
    Checklist checklist = checklistRepository.findById(checklistId)
        .orElseThrow(() -> new NotFoundChecklistException(ErrorCode.CHECKLIST_NOT_EXIST));

    return (checklist != null && userId.equals(checklist.getUserId()) && verifyUserToken(token,
        userId, studyId));
  }

  /**
   * 요청하는 사용자와 로그인한 사용자의 아이디 일치 여부 반환
   *
   * @param token   토큰
   * @param userId  사용자ID
   * @param studyId 스터디ID
   * @return 사용자ID와 토큰정보가 일치하고 스터디ID가 유효하면 true , 아니면 false
   */
  public boolean verifyUserToken(String token, Long userId, Long studyId){
    User userFromToken = getUserFromToken(token);
    if (userFromToken != null && verifyUserNStudy(token, studyId))
      return userService.findByIdAndToken(userFromToken.getId(), userId) != null;
    return false;
  }
}
