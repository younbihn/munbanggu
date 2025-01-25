package com.zerobase.munbanggu.user.service;


import static com.zerobase.munbanggu.common.type.ErrorCode.*;
import static com.zerobase.munbanggu.user.type.Role.INACTIVE;

import com.zerobase.munbanggu.auth.TokenProvider;
import com.zerobase.munbanggu.common.dto.TokenResponse;
import com.zerobase.munbanggu.common.exception.AlreadyRegisteredNicknameException;
import com.zerobase.munbanggu.common.exception.InvalidNicknameException;
import com.zerobase.munbanggu.common.exception.InvalidTokenException;
import com.zerobase.munbanggu.common.exception.NoPermissionException;
import com.zerobase.munbanggu.common.exception.NotFoundStudyException;
import com.zerobase.munbanggu.common.exception.NotFoundUserException;
import com.zerobase.munbanggu.common.exception.UnmatchedUserException;
import com.zerobase.munbanggu.common.exception.VerificationException;
import com.zerobase.munbanggu.common.exception.WithdrawnMemberAccessException;
import com.zerobase.munbanggu.common.exception.WrongPasswordException;
import com.zerobase.munbanggu.common.type.ErrorCode;
import com.zerobase.munbanggu.common.util.RedisUtil;
import com.zerobase.munbanggu.study.model.entity.Study;
import com.zerobase.munbanggu.study.repository.StudyRepository;
import com.zerobase.munbanggu.user.dto.FindUserInfoDto;
import com.zerobase.munbanggu.user.dto.GetUserDto;
import com.zerobase.munbanggu.user.dto.ResetPwDto;
import com.zerobase.munbanggu.user.dto.SignInDto;
import com.zerobase.munbanggu.user.dto.SmsVerificationInfo;
import com.zerobase.munbanggu.user.dto.UserRegisterDto;
import com.zerobase.munbanggu.user.model.entity.StudyUser;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.repository.StudyUserRepository;
import com.zerobase.munbanggu.user.repository.UserRepository;
import com.zerobase.munbanggu.user.type.LoginType;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final StudyUserRepository studyUserRepository;
    private final TokenProvider tokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;
    private final SendMessageService sendMessageService;

    public TokenResponse signIn(SignInDto signInDto) {
        User user = userRepository.findByEmail(signInDto.getEmail())
                .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));

        // 비밀번호 체크
        boolean isMatch = passwordEncoder.matches(signInDto.getPassword(), user.getPassword());
        if (!isMatch) {
            throw new WrongPasswordException(ErrorCode.WRONG_PASSWORD);
        }
        if (user.getRole().equals(INACTIVE)) {
            throw new WithdrawnMemberAccessException(ErrorCode.USER_WITHDRAWN);
        }

        //return "로그인 완료";
        String accessToken = tokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = tokenProvider.generateRefreshToken(user.getId(), user.getEmail(), user.getRole());

        tokenProvider.saveRefreshTokenInRedis(user.getEmail(), refreshToken);

        return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    public GetUserDto updateUser(Long id, GetUserDto getUserDto) { //유저정보 업데이트
        // 해당하는 유저가 존재하지 않을경우
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));
        if (user!=null) {
            user.setNickname(getUserDto.getNickname());
            user.setPhone(getUserDto.getPhone());
            user.setProfileImageUrl(getUserDto.getProfileImageUrl());
            userRepository.save(user);
            return getUserDto;
        }throw new NotFoundUserException(ErrorCode.USER_NOT_EXIST);
    }

    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    public User findByIdAndToken(Long tokenId, Long id) {
        if (tokenId.equals(id)) {
            return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));
        } else {
            throw new VerificationException(ErrorCode.INVALID_EMAIL);
        }
    }

    public GetUserDto getInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));
        return GetUserDto.builder().
                email(user.getEmail())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    public void registerUser(UserRegisterDto userDto) {
        // 닉네임 유효성 검사
        if (!userDto.getNickname().matches("[가-힣a-zA-Z0-9]{2,10}")) {
            throw new InvalidNicknameException(ErrorCode.INVALID_NICKNAME_FORMAT);
        }

        // 닉네임 중복 확인
        userRepository.findByNickname(userDto.getNickname())
                .ifPresent(u -> {
                    throw new AlreadyRegisteredNicknameException(ErrorCode.ALREADY_REGISTERED_NICKNAME);
                });

        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        User user = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(encodedPassword)
                .nickname(userDto.getNickname())
                .loginType(LoginType.EMAIL)
                .phone(userDto.getPhone())
                .profileImageUrl(userDto.getProfileImageUrl())
                .build();

        userRepository.save(user);
    }

    @Transactional
    public void updateProfileImage(Long userId, String imageUrl) {
        User siteUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        siteUser.setProfileImageUrl(imageUrl);
        userRepository.save(siteUser);
    }

    public String getProfileUrl(Long userId) {
        User siteUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        return siteUser.getProfileImageUrl();
    }

    /**
     * 이름과 핸드폰 번호가 일치하는 유저 찾기
     *
     * @param name  이름
     * @param phone 핸드폰번호
     * @return user
     */
    public User findByPhoneAndName(String name, String phone) {
        User user = userRepository.findByPhone(phone)
            .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));
        if (name.equals(user.getName())) {
            return user;
        } else {
            throw new NotFoundUserException(ErrorCode.USER_NOT_EXIST);
        }
    }

    /**
     * 이메일과 핸드폰 번호 일치하는 유저 찾기
     *
     * @param email 이메일
     * @param phone 핸드폰번호
     * @return user
     */
    public User findByEmailAndPhone(String email, String phone) {
        User user =userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));
        if (user.getPhone().equals(phone)) {
            return user;
        } else {
            throw new VerificationException(ErrorCode.INVALID_PHONE);
        }
    }

    /**
     * 아이디 찾기 신청 (핸드폰 인증메세지 발송)
     *
     * @param findUserInfoDto - name , phone
     * @return uuid(token)
     */
    public String requestFindId(FindUserInfoDto findUserInfoDto) {
        if (findByPhoneAndName(findUserInfoDto.getName(), findUserInfoDto.getPhone()) != null) {
            return sendMessageService.sendVerificationMessage(findUserInfoDto.getPhone());
        } else {
            throw new NotFoundUserException(ErrorCode.USER_NOT_EXIST);
        }
    }

    /**
     * 아이디 반환
     *
     * @param userId            사용자ID
     * @param findUserInfoDto - token,name,phone,inputCode
     * @return 아이디(이메일)반환
     */
    public String returnId(Long userId, FindUserInfoDto findUserInfoDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));
        SmsVerificationInfo info =
                redisUtil.getMsgVerificationInfo(findUserInfoDto.getToken());

        if (info == null) {
            throw new InvalidTokenException(INVALID_TOKEN);
        }

        if (!user.getEmail().equals(info.getEmail())) {
            throw new UnmatchedUserException(USER_UNMATCHED);
        }

        if (info.getVerificationCode().equals(findUserInfoDto.getInputCode())) {
            return findByPhoneAndName(findUserInfoDto.getName(),
                    findUserInfoDto.getPhone()).getEmail();
        }
        throw new VerificationException(INVALID_CODE);
    }

    /**
     * 비밀번호 재설정 요청
     *
     * @param userId            사용자ID
     * @param findUserInfoDto - email, phone
     * @return uuid(token)
     */
    public String requestResetPw(Long userId, FindUserInfoDto findUserInfoDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));
        User dtoUser = findByEmailAndPhone(findUserInfoDto.getEmail(), findUserInfoDto.getPhone());

        if (dtoUser == null) {
            throw new NotFoundUserException(USER_NOT_EXIST);
        }

        if (!user.getId().equals(dtoUser.getId())) {
            throw new UnmatchedUserException(USER_UNMATCHED);
        }

        return sendMessageService.sendVerificationMessage(findUserInfoDto.getPhone());
    }

    /**
     * 비밀번호 재설정
     *
     * @param userId       사용자ID
     * @param resetPwDto - token, newPassword, inputCode
     * @return 성공여부(T/F)
     */
    public boolean verifyResetPw(Long userId, ResetPwDto resetPwDto) {
        boolean result = true;

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));
        SmsVerificationInfo info = redisUtil.getMsgVerificationInfo(resetPwDto.getToken());

        if (info == null)
            throw new NotFoundUserException(USER_NOT_EXIST);

        if (!user.getEmail().equals(info.getEmail()))
            throw new UnmatchedUserException(USER_UNMATCHED);

        // 핸드폰 인증 번호가 같으면
        if (info.getVerificationCode().equals(resetPwDto.getInputCode())) {
            user.setPassword(resetPwDto.getNewPassword());
            userRepository.save(user);

            // 인증 정보 삭제
            redisUtil.deleteMsgVerificationInfo(resetPwDto.getToken());
        } else
            result = false;
        return result;
    }

    /**
     * 스터디 참여신청
     *
     * @param userId    사용자ID
     * @param studyId   스터디ID
     * @param password  비밀번호
     */
    public void joinStudy(Long userId, Long studyId, String password) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));
        Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new NotFoundStudyException(ErrorCode.STUDY_NOT_EXIST));
        if (study != null) {
            //private 인데 비밀번호 일치x
            if (!passwordEncoder.matches(password, study.getPassword()) &&
                    !study.isPublic_or_not()) {
                throw new VerificationException(WRONG_PASSWORD);
            }

            //이미 가입한 유저
            for (StudyUser studyList : studyUserRepository.findByUser(user)) {
                if (studyList.getStudy().equals(study)) {
                    throw new NotFoundStudyException(ALREADY_JOINED);
                }
            }

            // 모임 참여
            StudyUser studyUser = new StudyUser(user, study);
            studyUserRepository.save(studyUser);
        }
        throw new NotFoundStudyException(INVALID_USER_OR_STUDY);
    }

    /**
     * 스터디가입정보 삭제
     *
     * @param studyUser 유저의 스터디 가입정보
     */
    private void deleteStudyUser(StudyUser studyUser) {
        studyUserRepository.delete(studyUser);
    }

    /**
     * 스터디 탈퇴
     *
     * @param userId    사용자ID
     * @param studyId   스터디ID
     */
    public void withdrawStudy(Long userId, Long studyId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));
        Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new NotFoundStudyException(ErrorCode.STUDY_NOT_EXIST));

        // 회원이 스터디에 참여중이면 삭제
        for (StudyUser studyList : studyUserRepository.findByUser(user)) {
            if (studyList.getStudy().equals(study)) {
                deleteStudyUser(studyList);
            }
        }
        throw new NoPermissionException(NOT_PARTICIPATING);
    }
}
