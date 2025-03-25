package com.AddressBookAppMain.AddressBook.Service;

import com.AddressBookAppMain.AddressBook.DTO.LoginRequestDTO;
import com.AddressBookAppMain.AddressBook.DTO.LoginResponseDTO;
import com.AddressBookAppMain.AddressBook.DTO.UserDTO;
import com.AddressBookAppMain.AddressBook.Entity.User;
import com.AddressBookAppMain.AddressBook.Repository.UserRepository;
import com.AddressBookAppMain.AddressBook.Security.JwtUtil;
import com.AddressBookAppMain.AddressBook.Services.AuthService;
import com.AddressBookAppMain.AddressBook.Services.EmailService;
import jakarta.mail.MessagingException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtils;

    @Mock
    private EmailService emailService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @Mock
    private ValueOperations<String, String> valueOperations; // ✅ Mock ValueOperations

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");

        // ✅ Ensure redisTemplate.opsForValue() returns a mock ValueOperations object
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void testRegisterUser_Success() {
        UserDTO userDTO = new UserDTO("testUser", "test@example.com", "password123");

        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");

        String result = authService.registerUser(userDTO);

        assertEquals("User registered successfully!", result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegisterUser_EmailAlreadyExists() {
        UserDTO userDTO = new UserDTO("testUser", "test@example.com", "password123");

        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);

        String result = authService.registerUser(userDTO);

        assertEquals("Error: Email already registered!", result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testLoginUser_Success() throws MessagingException {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("test@example.com", "password123");

        when(userRepository.findByEmail(loginRequestDTO.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtils.generateToken(anyString())).thenReturn("mocked-jwt-token");

        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        LoginResponseDTO response = authService.loginUser(loginRequestDTO);

        assertEquals("Login successful! Check your email for the token.", response.getMessage());
        assertEquals("mocked-jwt-token", response.getToken());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoginUser_InvalidPassword() throws MessagingException {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("test@example.com", "wrongPassword");

        when(userRepository.findByEmail(loginRequestDTO.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        authService.loginUser(loginRequestDTO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoginUser_UserNotFound() throws MessagingException {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("notfound@example.com", "password123");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        authService.loginUser(loginRequestDTO);
    }

    @Test
    public void testLoginWithToken_Success() throws MessagingException {
        String token = "mocked-jwt-token";

        when(jwtUtils.getEmailFromToken(anyString())).thenReturn("test@example.com");
        when(redisTemplate.opsForValue().get(anyString())).thenReturn(token);
        when(jwtUtils.isTokenValid(anyString(), anyString())).thenReturn(true);

        boolean result = authService.loginWithToken(token);

        assertTrue(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoginWithToken_InvalidToken() throws MessagingException {
        String token = "invalid-token";

        when(jwtUtils.getEmailFromToken(anyString())).thenReturn("test@example.com");
        when(redisTemplate.opsForValue().get(anyString())).thenReturn(null);

        authService.loginWithToken(token);
    }

    @Test
    public void testForgotPassword_Success() throws MessagingException {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");

        boolean result = authService.forgotPassword(testUser.getEmail(), "newPassword");

        assertTrue(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testForgotPassword_UserNotFound() throws MessagingException {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        boolean result = authService.forgotPassword("notfound@example.com", "newPassword");

        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testResetPassword_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");

        boolean result = authService.resetPassword(testUser.getEmail(), "oldPassword", "newPassword");

        assertTrue(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResetPassword_IncorrectOldPassword() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        authService.resetPassword(testUser.getEmail(), "wrongOldPassword", "newPassword");
    }
}
