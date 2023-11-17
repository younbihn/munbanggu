package com.zerobase.munbanggu.user.UserImageUploadTest;

import com.zerobase.munbanggu.aws.S3Uploader;
import com.zerobase.munbanggu.config.auth.TokenProvider;
import com.zerobase.munbanggu.user.controller.UserController;
import com.zerobase.munbanggu.user.service.UserService;
import com.zerobase.munbanggu.util.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserImageUploadControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private S3Uploader s3Uploader;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setup() {
        userService = Mockito.mock(UserService.class);
        s3Uploader = Mockito.mock(S3Uploader.class);
        jwtService = Mockito.mock(JwtService.class);
        userController = new UserController(jwtService, userService, s3Uploader, tokenProvider);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }


    @Test
    public void testUploadOrUpdateProfileImage_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("imageFile", "filename.jpg", "image/jpeg", "some-image".getBytes());
        Long userId = 1L;
        String newImageUrl = "http://newimage.url";

        when(s3Uploader.uploadFile(file)).thenReturn(newImageUrl);
        doNothing().when(userService).updateProfileImage(userId, newImageUrl);

        mockMvc.perform(multipart("/api/user/my-page/" + userId + "/upload-profile-image").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string(newImageUrl));
    }

    @Test
    public void testUploadOrUpdateProfileImage_Failure() throws Exception {
        MockMultipartFile file = new MockMultipartFile("imageFile", "filename.jpg", "image/jpeg", "some-image".getBytes());
        Long userId = 1L;

        when(s3Uploader.uploadFile(file)).thenThrow(new IOException());

        mockMvc.perform(multipart("/api/user/my-page/" + userId + "/upload-profile-image").file(file))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to upload image"));
    }
}