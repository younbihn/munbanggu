package com.zerobase.munbanggu.user.UserRegisterTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.munbanggu.user.controller.UserController;
import com.zerobase.munbanggu.user.dto.UserRegisterDto;
import com.zerobase.munbanggu.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class UserRegisterControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = standaloneSetup(userController).build();
    }

    @Test
    void registerUser_success() throws Exception {
        // Instantiate ObjectMapper here
        ObjectMapper objectMapper = new ObjectMapper();

        // Given
        UserRegisterDto userDto = UserRegisterDto.builder()
                .name("test")
                .email("test@example.com")
                .password("password123")
                .nickname("testNick")
                .phone("1234567890")
                .profileImageUrl("http://example.com/profile.jpg")
                .build();

        String userDtoJson = objectMapper.writeValueAsString(userDto);

        // When & Then
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoJson))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }
}