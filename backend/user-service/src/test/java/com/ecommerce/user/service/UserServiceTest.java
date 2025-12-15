package com.ecommerce.user.service;

import com.ecommerce.user.dto.LoginRequest;
import com.ecommerce.user.dto.RegisterRequest;
import com.ecommerce.user.model.Role;
import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @TempDir
    Path tempDir;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(userService, "jwtUtil", jwtUtil);
        ReflectionTestUtils.setField(userService, "avatarDir", tempDir.toString());
    }

    @Test
    void register_shouldEncodePasswordAndPersistUser() {
        RegisterRequest request = new RegisterRequest("Alice", "alice@mail.com", "password123", Role.CLIENT);

        when(userRepository.existsByEmail("alice@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");

        String message = userService.register(request);

        assertThat(message).contains("succès");
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("hashed");
    }

    @Test
    void register_shouldThrowWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("Alice", "alice@mail.com", "password123", Role.CLIENT);
        when(userRepository.existsByEmail("alice@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("déjà utilisé");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_shouldReturnTokenWhenCredentialsAreValid() {
        User user = new User();
        user.setId("user-1");
        user.setEmail("alice@mail.com");
        user.setName("Alice");
        user.setPassword("hashed");
        user.setRole(Role.CLIENT);

        when(userRepository.findByEmail("alice@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken("user-1", "alice@mail.com", "CLIENT", "Alice")).thenReturn("jwt-token");

        LoginRequest request = new LoginRequest("alice@mail.com", "password123");

        var response = userService.login(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUserId()).isEqualTo("user-1");
        verify(jwtUtil).generateToken("user-1", "alice@mail.com", "CLIENT", "Alice");
    }

    @Test
    void uploadAvatar_shouldStoreFileAndUpdateUser() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(2048L);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getOriginalFilename()).thenReturn("avatar.png");
        InputStream inputStream = new ByteArrayInputStream("image".getBytes());
        when(file.getInputStream()).thenReturn(inputStream);

        User user = new User();
        user.setId("user-1");
        user.setEmail("alice@mail.com");

        when(userRepository.findByEmail("alice@mail.com")).thenReturn(Optional.of(user));

        String url = userService.uploadAvatar("alice@mail.com", file);

        assertThat(url).startsWith("/uploads/avatars/");
        verify(userRepository).save(user);
        try (var files = Files.list(tempDir)) {
            assertThat(files.count()).isEqualTo(1);
        }
    }

    //@Test
    //void intentionallyFailingTest_shouldAlwaysFail() {
     //  assertThat("expected to fail").isEqualTo("this will never match");
    //}
}
