package com.zerobase.munbanggu.user.UserRegisterTest;

import static org.mockito.Mockito.*;

import com.zerobase.munbanggu.user.dto.UserRegisterDto;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.repository.UserRepository;
import com.zerobase.munbanggu.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class UserRegisterServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void registerUser_success() {
        // Given
        UserRegisterDto userDto = UserRegisterDto.builder()
                .name("test")
                .email("test@example.com")
                .password("password123")
                .nickname("testNick")
                .phone("1234567890")
                .profileImageUrl("http://example.com/profile.jpg")
                .build();
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn(encodedPassword);

        // When
        userService.registerUser(userDto);

        // Then
        verify(userRepository).save(any(User.class));
    }
}