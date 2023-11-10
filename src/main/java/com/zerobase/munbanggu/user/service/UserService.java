package com.zerobase.munbanggu.user.service;


import static com.zerobase.munbanggu.type.ErrorCode.USER_NOT_EXIST;
import static com.zerobase.munbanggu.type.ErrorCode.USER_WITHDRAWN;
import static com.zerobase.munbanggu.type.ErrorCode.WRONG_PASSWORD;
import static com.zerobase.munbanggu.type.ErrorCode.EMAIL_NOT_EXISTS;
import static com.zerobase.munbanggu.user.type.Role.INACTIVE;

import com.zerobase.munbanggu.user.dto.GetUserDto;
import com.zerobase.munbanggu.user.dto.SignInDto;
import com.zerobase.munbanggu.user.dto.UserRegisterDto;
import com.zerobase.munbanggu.user.exception.UserException;

import com.zerobase.munbanggu.util.JwtService;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.repository.UserRepository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String signIn(SignInDto signInDto) {
        User user = userRepository.findByEmail(signInDto.getEmail())
                .orElseThrow(()-> new UserException(USER_NOT_EXIST));

        // 비밀번호 체크
        if(!signInDto.getPassword().equals(user.getPassword())){
            throw new UserException(WRONG_PASSWORD);

        }
        if(user.getRole().equals(INACTIVE)){
            throw new UserException(USER_WITHDRAWN);
        }

        //return "로그인 완료";
        return jwtService.createToken(user.getId(), user.getEmail(), user.getRole());
    }
    public GetUserDto updateUser(Long id, GetUserDto getUserDto) { //유저정보 업데이트
        // 해당하는 유저가 존재하지 않을경우
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(USER_NOT_EXIST));

        user.setNickname(getUserDto.getNickname());
        user.setEmail(getUserDto.getEmail());
        user.setPhone(getUserDto.getPhone());
        user.setProfileImageUrl(getUserDto.getProfileImageUrl());
        userRepository.save(user);
        return getUserDto;
    }
    public Optional<User> getUser(Long id){
        return userRepository.findById(id);
    }

    public GetUserDto getInfo(String email){

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
}
