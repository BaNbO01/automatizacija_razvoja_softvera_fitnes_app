
package rs.ac.bg.fon.nst.fitnes.service.auth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import rs.ac.bg.fon.nst.fitnes.domain.Role;
import rs.ac.bg.fon.nst.fitnes.domain.User;
import rs.ac.bg.fon.nst.fitnes.dto.LoginRequest;
import rs.ac.bg.fon.nst.fitnes.dto.RegisterRequest;
import rs.ac.bg.fon.nst.fitnes.dto.UserResponse;
import rs.ac.bg.fon.nst.fitnes.exception.DuplicateEntryException;
import rs.ac.bg.fon.nst.fitnes.exception.ResourceNotFoundException;
import rs.ac.bg.fon.nst.fitnes.mapper.UserMapper;
import rs.ac.bg.fon.nst.fitnes.repo.UserRepository;
import rs.ac.bg.fon.nst.fitnes.security.JwtTokenProvider;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import rs.ac.bg.fon.nst.fitnes.service.auth.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Role testRole;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
       
        testUser = new User();
        testUser.setId(1);
        testUser.setEmail("test@example.com");
        testUser.setPassword("encoded_password");
        testUser.setCreatedAt(LocalDateTime.now());

        testRole = new Role("TRENER");
        testRole.setId(1);
        testRole.setUser(testUser);
        testUser.setRole(testRole);

        registerRequest = new RegisterRequest("test_user", "test@example.com", "password123", "trener");
        loginRequest = new LoginRequest("test@example.com", "password123");
    }

    @Test
    void testRegisterUser_Success() {
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty()); // Provera da korisnik ne postoji
        when(userMapper.toUser(any(RegisterRequest.class))).thenReturn(testUser); // Mapiranje DTO-a u domen objekat
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password"); // Enkodiranje lozinke
        when(userRepository.save(any(User.class))).thenReturn(testUser); // Čuvanje korisnika
        when(userMapper.toUserResponse(any(User.class))).thenReturn(new UserResponse(1, "test@example.com", null, null)); // Mapiranje u Response DTO

        
        UserResponse result = authService.registerUser(registerRequest);

       
        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());

       
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toUser(any(RegisterRequest.class));
        verify(userMapper, times(1)).toUserResponse(any(User.class));
        verify(passwordEncoder, times(1)).encode(anyString());
    }

    @Test
    void testRegisterUser_DuplicateEmail_ThrowsException() {
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        
        assertThrows(DuplicateEntryException.class, () -> authService.registerUser(registerRequest));

        
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLoginUser_Success() {
       
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("mock_jwt_token");

       
        String token = authService.loginUser(loginRequest);

       
        assertNotNull(token);
        assertEquals("mock_jwt_token", token);

        
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, times(1)).generateToken(authentication);
       
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testLogoutUser_Success() {
       
        authService.logoutUser();

      
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testGetUserByEmail_Success() {
      
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(userMapper.toUserResponse(any(User.class))).thenReturn(new UserResponse(1, "test@example.com", null, null));

     
        UserResponse result = authService.getUserByEmail("test@example.com");

      
        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());

       
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userMapper, times(1)).toUserResponse(any(User.class));
    }

    @Test
    void testGetUserByEmail_NotFound_ThrowsException() {
       
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

       
        assertThrows(ResourceNotFoundException.class, () -> authService.getUserByEmail("nepostojeci@email.com"));

       
        verify(userMapper, never()).toUserResponse(any(User.class));
    }
}