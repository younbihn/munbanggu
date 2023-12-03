package com.zerobase.munbanggu.common.util;

import com.zerobase.munbanggu.auth.TokenProvider;
import com.zerobase.munbanggu.common.exception.NotFoundChecklistException;
import com.zerobase.munbanggu.common.exception.NotFoundStudyException;
import com.zerobase.munbanggu.common.exception.NotFoundUserException;
import com.zerobase.munbanggu.common.type.ErrorCode;
import com.zerobase.munbanggu.study.model.entity.Checklist;
import com.zerobase.munbanggu.study.repository.ChecklistRepository;
import com.zerobase.munbanggu.study.repository.StudyRepository;
import com.zerobase.munbanggu.study.service.StudyService;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.repository.UserRepository;
import com.zerobase.munbanggu.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidationService {
  private final TokenProvider tokenProvider;
  private final ChecklistRepository checklistRepository;
  private final StudyRepository studyRepository;
  private final UserRepository userRepository;
  private final UserService userService;

  /**
   * 토큰에서 유저 정보 추출하기
   *
   * @param token 토큰
   * @return user
   */
  public User getUserFromToken(String token){
    return userRepository.findById(tokenProvider.getId(token))
        .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));
  }

  /**
   * 토큰과 스터디 아이디 유효성 검증
   *
   * @param token   토큰
   * @param studyId 스터디ID
   * @return 일치여부 (T/F)
   */
  public boolean verifyUserNStudy(String token, Long studyId){
    getUserFromToken(token);

    return studyRepository.findById(studyId).isPresent();
  }

  /**
   * 요청하는 사용자와 체크리스트 생성자의 일치여부 반환
   *
   * @param token         토큰
   * @param userId        사용자ID
   * @param studyId       스터디ID
   * @param checklistId   체크리스트ID
   * @return 일치여부 (T/F)
   */
  public boolean verifyUserNListcreator(String token,Long userId,Long studyId,Long checklistId) {
    Checklist checklist = checklistRepository.findById(checklistId)
        .orElseThrow(() -> new NotFoundChecklistException(ErrorCode.CHECKLIST_NOT_EXIST));

    return (userId.equals(checklist.getUserId()) && verifyUserToken(token, userId, studyId));
  }

  /**
   * 요청하는 사용자와 로그인한 사용자의 아이디 일치 여부 반환
   *
   * @param token   토큰
   * @param userId  사용자ID
   * @param studyId 스터디ID
   * @return 일치여부 (T/F)
   */
  public boolean verifyUserToken(String token, Long userId, Long studyId){
    User userFromToken = getUserFromToken(token);

    return (verifyUserNStudy(token, studyId) &&
        userService.findByIdAndToken(userFromToken.getId(), userId) != null);
  }
}
