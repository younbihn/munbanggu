package com.zerobase.munbanggu.user.controller;


import com.zerobase.munbanggu.aws.S3Uploader;
import com.zerobase.munbanggu.config.auth.TokenProvider;
import com.zerobase.munbanggu.user.dto.GetUserDto;
import com.zerobase.munbanggu.user.dto.UserRegisterDto;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.service.UserService;
import com.zerobase.munbanggu.util.JwtService;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private static final String AUTH_HEADER = "Authorization";
    private final JwtService jwtService;
    private final UserService userService;
    private final S3Uploader s3Uploader;
    private final TokenProvider tokenProvider;

    @PutMapping("/{user_id}")
    public ResponseEntity<?> updateUser( @RequestHeader(name = AUTH_HEADER) String token,
            @RequestBody GetUserDto getUserDto){

        Optional<User> user = userService.getUser(tokenProvider.getId(token));
        if (user.isPresent()) {
            // 유효한 토큰으로 사용자 정보 가져오기
            return ResponseEntity.ok(
                    userService.updateUser(
                            tokenProvider.getId(token),
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

    @PostMapping("my-page/{userId}/upload-profile-image")
    public ResponseEntity<?> uploadOrUpdateProfileImage(@PathVariable Long userId, @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            // 기존 이미지 URL 가져오기 및 삭제
            String oldImageUrl = userService.getProfileUrl(userId);
            if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                s3Uploader.deleteFile(oldImageUrl);
            }

            // 새 이미지 업로드 및 URL 반환
            String newImageUrl = s3Uploader.uploadFile(imageFile);

            // SiteUser의 profileImg 필드 업데이트
            userService.updateProfileImage(userId, newImageUrl);

            return new ResponseEntity<>(newImageUrl, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to upload image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}