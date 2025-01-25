package com.zerobase.munbanggu.user.UserImageUploadTest;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.repository.UserRepository;
import com.zerobase.munbanggu.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class UserImageUploadServiceTeset {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testUpdateProfileImage() {
        Long userId = 1L;
        String imageUrl = "http://newimage.url";
        User mockUser = new User();
        mockUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        userService.updateProfileImage(userId, imageUrl);

        assertEquals(imageUrl, mockUser.getProfileImageUrl());
        verify(userRepository).save(mockUser);
    }

    @Test
    public void testGetProfileUrl() {
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setProfileImageUrl("http://example.com/image.jpg");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        String profileUrl = userService.getProfileUrl(userId);

        assertEquals("http://example.com/image.jpg", profileUrl);
    }
}
