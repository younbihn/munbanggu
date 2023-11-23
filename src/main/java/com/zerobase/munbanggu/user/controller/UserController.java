package com.zerobase.munbanggu.user.controller;

import com.zerobase.munbanggu.aws.S3Uploader;
import com.zerobase.munbanggu.auth.TokenProvider;
import com.zerobase.munbanggu.study.dto.JoinStudyDto;
import com.zerobase.munbanggu.user.dto.GetUserDto;
import com.zerobase.munbanggu.user.dto.UserRegisterDto;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.service.UserService;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private static final String AUTH_HEADER = "Authorization";
    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final S3Uploader s3Uploader;

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

    /**
     * 사용자 정보 반환
     *
     * @param userId 사용자ID
     * @return GetUserDto
     */
    @GetMapping("/{user_id}")
    public ResponseEntity<GetUserDto> getUserInfo(@PathVariable("user_id")Long userId){
        return ResponseEntity.ok(userService.getInfo(userId));
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

    /**
     * 스터디 신청
     *
     * @param userId    사용자ID
     * @param studyId   스터디ID
     * @param joinStudyDto  비밀번호
     */
    @PostMapping("/{user_id}/study/{study_id}")
    public ResponseEntity<String> joinStudy(
        @PathVariable("user_id") Long userId,
        @PathVariable("study_id") Long studyId,
        @RequestParam(required = false)
        JoinStudyDto joinStudyDto) {

        userService.joinStudy(userId,studyId,joinStudyDto.getPassword());
        return ResponseEntity.ok().body("참여완료");
    }

    /**
     * 스터디 탈퇴
     * @param userId    사용자ID
     * @param studyId   스터디ID
     */
    @DeleteMapping("/{user_id}/study/{study_id}")
    public ResponseEntity<?> withdrawStudy(
        @PathVariable("user_id") Long userId,
        @PathVariable("study_id") Long studyId){

        userService.withdrawStudy(userId,studyId);
        return ResponseEntity.ok().body("탈퇴완료");
    }

}
