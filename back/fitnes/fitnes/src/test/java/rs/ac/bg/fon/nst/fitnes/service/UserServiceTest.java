/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import rs.ac.bg.fon.nst.fitnes.domain.FitnesDnevnik;
import rs.ac.bg.fon.nst.fitnes.domain.Role;
import rs.ac.bg.fon.nst.fitnes.domain.User;
import rs.ac.bg.fon.nst.fitnes.dto.FitnesDnevnikResponse;
import rs.ac.bg.fon.nst.fitnes.dto.UserResponse;
import rs.ac.bg.fon.nst.fitnes.exception.ResourceNotFoundException;
import rs.ac.bg.fon.nst.fitnes.exception.UnauthorizedAccessException;
import rs.ac.bg.fon.nst.fitnes.mapper.FitnesDnevnikMapper;
import rs.ac.bg.fon.nst.fitnes.mapper.UserMapper;
import rs.ac.bg.fon.nst.fitnes.repo.FitnesDnevnikRepository;
import rs.ac.bg.fon.nst.fitnes.repo.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;


    @Mock
    private UserRepository userRepository;
    @Mock
    private FitnesDnevnikRepository fitnesDnevnikRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private FitnesDnevnikMapper fitnesDnevnikMapper;

    
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    private User mockLoggedInUser;
    private User mockTrainer;
    private User mockVezbac;
    private FitnesDnevnik mockFitnesDnevnik;
    private UserResponse mockUserResponse;
    private FitnesDnevnikResponse mockFitnesDnevnikResponse;

    @BeforeEach
    void setUp() {
        
        Role userRole = new Role();
        userRole.setRole("VEZBAC");
        mockLoggedInUser = new User();
        mockLoggedInUser.setId(1);
        mockLoggedInUser.setEmail("vezbac@example.com");
        mockLoggedInUser.setRole(userRole);

        Role trainerRole = new Role();
        trainerRole.setRole("TRENER");
        mockTrainer = new User();
        mockTrainer.setId(2);
        mockTrainer.setEmail("trener@example.com");
        mockTrainer.setRole(trainerRole);

        Role vezbacRole = new Role();
        vezbacRole.setRole("VEZBAC");
        mockVezbac = new User();
        mockVezbac.setId(3);
        mockVezbac.setEmail("drugi_vezbac@example.com");
        mockVezbac.setRole(vezbacRole);

        mockFitnesDnevnik = new FitnesDnevnik();
        mockFitnesDnevnik.setId(1L);
        mockFitnesDnevnik.setNaziv("Dnevnik 1");
        mockFitnesDnevnik.setVezbac(mockLoggedInUser);

        mockUserResponse = new UserResponse();
        mockUserResponse.setId(2);
        mockUserResponse.setEmail("trener@example.com");

        mockFitnesDnevnikResponse = new FitnesDnevnikResponse();
        mockFitnesDnevnikResponse.setId(1L);
        mockFitnesDnevnikResponse.setNaslov("Dnevnik 1");
    }

   
    private void mockAuthenticatedUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("vezbac@example.com");
        when(authentication.getName()).thenReturn("vezbac@example.com");
        when(userRepository.findByEmail("vezbac@example.com")).thenReturn(Optional.of(mockLoggedInUser));
        SecurityContextHolder.setContext(securityContext);
    }



    @Test
    void testGetLoggedInUserDnevnici_Success() {
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockAuthenticatedUser();

            Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
            Page<FitnesDnevnik> fitnesDnevnikPage = new PageImpl<>(Collections.singletonList(mockFitnesDnevnik), pageable, 1);

            when(fitnesDnevnikRepository.findByVezbac(mockLoggedInUser, pageable)).thenReturn(fitnesDnevnikPage);
            when(fitnesDnevnikMapper.toFitnesDnevnikResponse(mockFitnesDnevnik)).thenReturn(mockFitnesDnevnikResponse);

            Page<FitnesDnevnikResponse> result = userService.getLoggedInUserDnevnici(0, 10);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(mockFitnesDnevnikResponse.getNaslov(), result.getContent().get(0).getNaslov());

            verify(fitnesDnevnikRepository, times(1)).findByVezbac(mockLoggedInUser, pageable);
            verify(fitnesDnevnikMapper, times(1)).toFitnesDnevnikResponse(mockFitnesDnevnik);
        }
    }

    @Test
    void testGetLoggedInUserDnevnici_Unauthorized() {
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(false);

            assertThrows(UnauthorizedAccessException.class, () -> userService.getLoggedInUserDnevnici(0, 10));

            verifyNoInteractions(fitnesDnevnikRepository);
            verifyNoInteractions(userRepository);
        }
    }
    
   

    @Test
    void testGetTrainers_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> trainerPage = new PageImpl<>(Collections.singletonList(mockTrainer), pageable, 1);
        when(userRepository.findByRole_Role("TRENER", pageable)).thenReturn(trainerPage);
        when(userMapper.toUserResponse(mockTrainer)).thenReturn(mockUserResponse);

        Page<UserResponse> result = userService.getTrainers(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(mockUserResponse.getEmail(), result.getContent().get(0).getEmail());

        verify(userRepository, times(1)).findByRole_Role("TRENER", pageable);
        verify(userMapper, times(1)).toUserResponse(mockTrainer);
    }
    
    

    @Test
    void testGetVezbaci_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> vezbacPage = new PageImpl<>(Collections.singletonList(mockVezbac), pageable, 1);
        when(userRepository.findByRole_Role("VEZBAC", pageable)).thenReturn(vezbacPage);
        when(userMapper.toUserResponse(mockVezbac)).thenReturn(mockUserResponse);

        Page<UserResponse> result = userService.getVezbaci(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(mockUserResponse.getEmail(), result.getContent().get(0).getEmail());

        verify(userRepository, times(1)).findByRole_Role("VEZBAC", pageable);
        verify(userMapper, times(1)).toUserResponse(mockVezbac);
    }

   

    @Test
    void testDeleteTrainer_Success() {
        when(userRepository.findById(2)).thenReturn(Optional.of(mockTrainer));
        
        userService.deleteTrainer(2);

        verify(userRepository, times(1)).findById(2);
        verify(userRepository, times(1)).delete(mockTrainer);
    }

    @Test
    void testDeleteTrainer_NotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.deleteTrainer(99));

        assertEquals("Trener nije pronađen sa id : '99'", exception.getMessage());
        verify(userRepository, times(1)).findById(99);
        verify(userRepository, never()).delete(any());
    }

   

    @Test
    void testDeleteVezbac_Success() {
        when(userRepository.findById(3)).thenReturn(Optional.of(mockVezbac));

        userService.deleteVezbac(3);

        verify(userRepository, times(1)).findById(3);
        verify(userRepository, times(1)).delete(mockVezbac);
    }

    @Test
    void testDeleteVezbac_NotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.deleteVezbac(99));

        assertEquals("Vežbač nije pronađen sa id : '99'", exception.getMessage());
        verify(userRepository, times(1)).findById(99);
        verify(userRepository, never()).delete(any());
    }
}
