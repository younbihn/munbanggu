package com.zerobase.munbanggu.user.service;

import static com.zerobase.munbanggu.type.ErrorCode.USER_NOT_EXIST;
import static com.zerobase.munbanggu.type.ErrorCode.USER_WITHDRAWN;
import static com.zerobase.munbanggu.type.ErrorCode.WRONG_PASSWORD;
import static com.zerobase.munbanggu.user.type.Role.INACTIVE;

import com.zerobase.munbanggu.user.dto.SignInDto;
import com.zerobase.munbanggu.user.dto.UserUpdateDto;
import com.zerobase.munbanggu.user.exception.LoginException;
import com.zerobase.munbanggu.util.JwtService;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    private final JwtService jwtService;

    public String signIn(SignInDto signInDto) {
        User user = userRepository.findByEmail(signInDto.getEmail())
                .orElseThrow(()-> new LoginException(USER_NOT_EXIST));

        // 비밀번호 체크
        if(!signInDto.getPassword().equals(user.getPassword())){
            throw new LoginException(WRONG_PASSWORD);

        }
        if(user.getRole().equals(INACTIVE)){
            throw new LoginException(USER_WITHDRAWN);
        }

        //return "로그인 완료";
        return jwtService.createToken(user.getId(), user.getEmail(), user.getRole());
    }
    public UserUpdateDto updateUser(Long id, UserUpdateDto userUpdateDto) { //유저정보 업데이트
        // 해당하는 유저가 존재하지 않을경우
        User user = userRepository.findById(id)
                .orElseThrow(() -> new LoginException(USER_NOT_EXIST));

        user.setNickname(userUpdateDto.getNickname());
        user.setEmail(userUpdateDto.getEmail());
        user.setPhone(userUpdateDto.getPhone());
        user.setProfileImageUrl(userUpdateDto.getProfileImageUrl());
        userRepository.save(user);
        return userUpdateDto;
    }
    public Optional<User> getUser(Long id){
        return userRepository.findById(id);
    }
}
