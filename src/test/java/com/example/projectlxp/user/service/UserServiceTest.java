package com.example.projectlxp.user.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.projectlxp.user.dto.UserJoinRequestDTO;
import com.example.projectlxp.user.entity.User;
import com.example.projectlxp.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock private UserRepository userRepository;

    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserServiceImpl userService;

    @Test
    @DisplayName("회원가입 성공")
    void join_Success() {

        UserJoinRequestDTO requestDTO =
                new UserJoinRequestDTO(
                        "testUser", "test@exmaple.com", "password123", "STUDENT", null);
        // 2. Mock 객체 행동 정의 (가장 중요!)
        // (1) "findByEmail이 호출되면, 결과 없음(Optional.empty)을 반환해라"
        when(userRepository.findByEmail(requestDTO.getEmail())).thenReturn(Optional.empty());

        // (2) "passwordEncoder.encode가 호출되면, 'encodedPW'라고 반환해라"
        when(passwordEncoder.encode(requestDTO.getPassword())).thenReturn("encodedPW");

        // (3) "userRepository.save가 호출되면, 그냥 User 객체를 반환해라"
        // (User 엔티티의 toEntity/createUser 메서드가 필요함)
        when(userRepository.save(any(User.class))).thenReturn(null); // (임시로 null 반환)

        // when (무엇을 할 때)
        // [테스트 대상 실행]
        userService.join(requestDTO);

        // then (결과는?)
        // [검증]
        // "userRepository.save()가 '정확히 1번' 호출되었는가?"
        verify(userRepository, times(1)).save(any(User.class));
    }
}
