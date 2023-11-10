package com.zerobase.munbanggu.user.controller;


import com.zerobase.munbanggu.user.dto.GetUserDto;
import com.zerobase.munbanggu.user.dto.UserRegisterDto;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.service.UserService;
import com.zerobase.munbanggu.util.JwtService;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private static final String AUTH_HEADER = "Authorization";
    private final JwtService jwtService;
    private final UserService userService;
    @PutMapping("/{user_id}")
    public ResponseEntity<?> updateUser( @RequestHeader(name = AUTH_HEADER) String token,
            @RequestBody GetUserDto getUserDto){

        Optional<User> user = userService.getUser(jwtService.getIdFromToken(token));
        if (user.isPresent()) {
            // 유효한 토큰으로 사용자 정보 가져오기
            return ResponseEntity.ok(
                    userService.updateUser(
                            jwtService.getIdFromToken(token),
                            getUserDto
                    )
            );
        }else {
            // 토큰이 유효하지 않은 경우 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 실패");
        }
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<GetUserDto> getUserInfo(@RequestBody Map<String,String> req){
        return ResponseEntity.ok(userService.getInfo(req.get("email")));
    }
  
    @Transactional(isolation=Isolation.SERIALIZABLE)
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterDto userDto) {
        userService.registerUser(userDto);
        return ResponseEntity.ok("User registered successfully");
    }
}
