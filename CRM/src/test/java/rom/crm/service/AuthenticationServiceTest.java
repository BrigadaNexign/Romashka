package rom.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import rom.crm.dto.auth.JwtAuthenticationResponse;
import rom.crm.dto.auth.SignInRequest;
import rom.crm.dto.auth.SignUpRequest;
import rom.crm.entity.User;
import rom.crm.entity.Role;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private SignUpRequest signUpRequest;
    private SignInRequest signInRequest;
    private User user;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequest(
                "testUser",
                "test@example.com",
                "79991234567",
                "password",
                Role.SUBSCRIBER
        );

        signInRequest = new SignInRequest(
                "testUser",
                "password"
        );

        user = User.builder()
                .username("testUser")
                .email("test@example.com")
                .msisdn("79991234567")
                .password("encodedPassword")
                .role(Role.SUBSCRIBER)
                .build();

        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testUser")
                .password("encodedPassword")
                .authorities(Role.SUBSCRIBER.name())
                .build();
    }

    @Test
    void signUp_returnJwtValidRequest() {
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userService.create(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("testToken");

        JwtAuthenticationResponse response = authenticationService.signUp(signUpRequest);

        assertNotNull(response);
        assertEquals("testToken", response.getToken());

        verify(passwordEncoder).encode(signUpRequest.getPassword());
        verify(userService).create(any(User.class));
        verify(jwtService).generateToken(any(User.class));
    }

    @Test
    void signIn_returnJwtResponseValidCredentials() {
        when(userService.userDetailsService()).thenReturn(userDetailsService);
        when(userService.userDetailsService().loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("testToken");

        JwtAuthenticationResponse response = authenticationService.signIn(signInRequest);

        assertNotNull(response);
        assertEquals("testToken", response.getToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService.userDetailsService()).loadUserByUsername(signInRequest.getUsername());
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void signIn_callAuthenticationManager() {
        when(userService.userDetailsService()).thenReturn(userDetailsService);
        when(userService.userDetailsService().loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("testToken");

        authenticationService.signIn(signInRequest);

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.getUsername(),
                        signInRequest.getPassword()
                )
        );
    }

    @Test
    void signUp_encodePassword() {
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userService.create(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("testToken");

        authenticationService.signUp(signUpRequest);

        verify(passwordEncoder).encode(signUpRequest.getPassword());
        verify(userService).create(argThat(u -> u.getPassword().equals("encodedPassword")));
    }
}