package com.zerobase.munbanggu.user.service;


import static com.zerobase.munbanggu.type.ErrorCode.INVALID_CODE;
import static com.zerobase.munbanggu.type.ErrorCode.INVALID_EMAIL;
import static com.zerobase.munbanggu.type.ErrorCode.INVALID_PHONE;
import static com.zerobase.munbanggu.type.ErrorCode.EMAIL_NOT_EXISTS;
import static com.zerobase.munbanggu.type.ErrorCode.USER_NOT_EXIST;
import static com.zerobase.munbanggu.type.ErrorCode.USER_WITHDRAWN;
import static com.zerobase.munbanggu.type.ErrorCode.WRONG_PASSWORD;
import static com.zerobase.munbanggu.user.type.Role.INACTIVE;

import com.zerobase.munbanggu.config.auth.TokenProvider;
import com.zerobase.munbanggu.dto.TokenResponse;
import com.zerobase.munbanggu.user.dto.FindUserInfoDto;
import com.zerobase.munbanggu.user.dto.GetUserDto;
import com.zerobase.munbanggu.user.dto.ResetPwDto;
import com.zerobase.munbanggu.user.dto.SignInDto;
import com.zerobase.munbanggu.user.dto.SmsVerificationInfo;
import com.zerobase.munbanggu.user.dto.UserRegisterDto;
import com.zerobase.munbanggu.user.exception.InvalidNicknameException;
import com.zerobase.munbanggu.user.exception.NicknameAlreadyExistsException;
import com.zerobase.munbanggu.user.exception.UserException;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.repository.UserRepository;
import com.zerobase.munbanggu.user.type.AuthenticationStatus;
import com.zerobase.munbanggu.util.RedisUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;
    private final SendMessageService sendMessageService;

    public TokenResponse signIn(SignInDto signInDto) {
        User user = userRepository.findByEmail(signInDto.getEmail())
                .orElseThrow(() -> new UserException(USER_NOT_EXIST));

        // 비밀번호 체크
        boolean isMatch = passwordEncoder.matches(signInDto.getPassword(), user.getPassword());
        if (!isMatch) {
            throw new UserException(WRONG_PASSWORD);
        }
        if (user.getRole().equals(INACTIVE)) {
            throw new UserException(USER_WITHDRAWN);
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
                .orElseThrow(() -> new UserException(USER_NOT_EXIST));

        user.setNickname(getUserDto.getNickname());
        user.setPhone(getUserDto.getPhone());
        user.setProfileImageUrl(getUserDto.getProfileImageUrl());
        userRepository.save(user);
        return getUserDto;
    }

    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    public User findByIdAndToken(Long tokenId, Long id){
        if (tokenId.equals(id))
            return userRepository.findById(id).orElseThrow(() -> new UserException(USER_NOT_EXIST));
        else
            throw new UserException(INVALID_EMAIL);
    }
    public GetUserDto getInfo(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(EMAIL_NOT_EXISTS));

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
            throw new InvalidNicknameException("Invalid nickname format");
        }

        // 닉네임 중복 확인
        userRepository.findByNickname(userDto.getNickname())
                .ifPresent(u -> {
                    throw new NicknameAlreadyExistsException("Nickname already exists");
                });

        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        User user = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(encodedPassword)
                .nickname(userDto.getNickname())
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
     * @param name
     * @param phone
     * @return user
     */
    public User findByPhoneAndName(String name, String phone) {
        User user = userRepository.findByPhone(phone)
            .orElseThrow( () -> new UserException(INVALID_PHONE));

        if (name.equals(user.getName())) {
            return user;
        }
        else
            throw new UserException(USER_NOT_EXIST);
    }

    /**
     * 이메일과 핸드폰 번호 일치하는 유저 찾기
     * @param email
     * @param phone
     * @return user
     */
    public User findByEmailAndPhone(String email, String phone) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserException(EMAIL_NOT_EXISTS));  //이메일이 db에 있는지 확인

        if (user.getPhone().equals(phone))
            return user;
        else
            throw new UserException(INVALID_PHONE);
    }

    /**
     * 아이디 찾기 신청 (핸드폰 인증메세지 발송)
     * @param findUserInfoDto - name , phone
     * @return uuid(token)
     */
    public String requestFindId(FindUserInfoDto findUserInfoDto) {
        if (findByPhoneAndName(findUserInfoDto.getName(), findUserInfoDto.getPhone()) != null)
            return sendMessageService.sendVerificationMessage(findUserInfoDto.getPhone());
        else
            throw new UserException(USER_NOT_EXIST);
    }

    /**
     * 아이디 반환
     * @param findUserInfoDto - token,name,phone,inputCode
     * @return userEmail
     */
    public String returnId(FindUserInfoDto findUserInfoDto) {
        SmsVerificationInfo info =
            redisUtil.getMsgVerificationInfo(findUserInfoDto.getToken());

        if (info != null && info.getVerificationCode().equals(findUserInfoDto.getInputCode())) {
            return findByPhoneAndName(findUserInfoDto.getName(),
                                        findUserInfoDto.getPhone()).getEmail();
        } else
            throw new UserException(INVALID_CODE);
    }

    /**
     * 비밀번호 재설정 요청
     * @param findUserInfoDto - email, phone
     * @return uuid(token)
     */
    public String requestResetPw(FindUserInfoDto findUserInfoDto) {
        if (findByEmailAndPhone(findUserInfoDto.getEmaill(), findUserInfoDto.getPhone()) != null)
                return sendMessageService.sendVerificationMessage(findUserInfoDto.getPhone());
        else
            throw new UserException(USER_NOT_EXIST);
    }

    /**
     * 비밀번호 재설정
     * @param resetPwDto - token, newPassword, inputCode
     * @return AuthenticationStatus (SUCCESS/FAIL)
     */
    public AuthenticationStatus verifyResetPw(ResetPwDto resetPwDto) {
        SmsVerificationInfo info = redisUtil.getMsgVerificationInfo(resetPwDto.getToken());
        // 핸드폰 인증 번호가 같으면
        if( info != null && info.getVerificationCode().equals(resetPwDto.getInputCode())) {
            User user = userRepository.findByEmail(info.getEmail())
                .orElseThrow(() -> new UserException(EMAIL_NOT_EXISTS));
            user.setPassword(resetPwDto.getNewPassword());
            userRepository.save(user);

            // 인증 정보 삭제
            redisUtil.deleteMsgVerificationInfo(resetPwDto.getToken());
            return AuthenticationStatus.SUCCESS;
        }
        else
            return AuthenticationStatus.FAIL;
    }
}
