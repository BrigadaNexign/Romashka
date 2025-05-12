package rom.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import rom.crm.entity.User;
import rom.crm.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .build();
    }

    @Test
    void save_callRepositorySave() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.save(testUser);

        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).save(testUser);
    }

    @Test
    void create_saveUser() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.create(testUser);

        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).existsByUsername(testUser.getUsername());
        verify(userRepository).existsByEmail(testUser.getEmail());
        verify(userRepository).save(testUser);
    }

    @Test
    void create_throwException_usernameExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.create(testUser);
        });

        assertEquals("Пользователь с таким именем уже существует", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void create_throwException_emailExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.create(testUser);
        });

        assertEquals("Пользователь с таким email уже существует", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void getByUsername_returnUser() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        User result = userService.getByUsername(testUser.getUsername());

        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).findByUsername(testUser.getUsername());
    }

    @Test
    void getByUsername_throwException_userNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.getByUsername("nonexistent");
        });

        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void userDetailsService_returnUserDetailsService() {
        UserDetailsService userDetailsService = userService.userDetailsService();

        assertNotNull(userDetailsService);
    }

    @Test
    void userDetailsService_useGetByUsername() {
        UserDetailsService userDetailsService = userService.userDetailsService();
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        userDetailsService.loadUserByUsername(testUser.getUsername());

        verify(userRepository).findByUsername(testUser.getUsername());
    }
}