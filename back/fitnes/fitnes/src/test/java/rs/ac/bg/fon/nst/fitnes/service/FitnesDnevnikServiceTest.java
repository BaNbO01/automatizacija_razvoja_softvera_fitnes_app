package rs.ac.bg.fon.nst.fitnes.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import rs.ac.bg.fon.nst.fitnes.domain.FitnesDnevnik;
import rs.ac.bg.fon.nst.fitnes.domain.StavkaDnevnika;
import rs.ac.bg.fon.nst.fitnes.domain.User;
import rs.ac.bg.fon.nst.fitnes.dto.FitnesDnevnikRequest;
import rs.ac.bg.fon.nst.fitnes.dto.FitnesDnevnikResponse;
import rs.ac.bg.fon.nst.fitnes.dto.StavkaDnevnikaRequest;
import rs.ac.bg.fon.nst.fitnes.dto.StavkaDnevnikaResponse;
import rs.ac.bg.fon.nst.fitnes.exception.ResourceNotFoundException;
import rs.ac.bg.fon.nst.fitnes.exception.UnauthorizedAccessException;
import rs.ac.bg.fon.nst.fitnes.mapper.FitnesDnevnikMapper;
import rs.ac.bg.fon.nst.fitnes.mapper.StavkaDnevnikaMapper;
import rs.ac.bg.fon.nst.fitnes.repo.FitnesDnevnikRepository;
import rs.ac.bg.fon.nst.fitnes.repo.StavkaDnevnikaRepository;
import rs.ac.bg.fon.nst.fitnes.repo.UserRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList; // Dodat je import za ArrayList

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FitnesDnevnikServiceTest {

    @InjectMocks
    private FitnesDnevnikService fitnesDnevnikService;

  
    @Mock
    private FitnesDnevnikRepository fitnesDnevnikRepository;
    @Mock
    private StavkaDnevnikaRepository stavkaDnevnikaRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FitnesDnevnikMapper fitnesDnevnikMapper;
    @Mock
    private StavkaDnevnikaMapper stavkaDnevnikaMapper;

   
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    private User authenticatedUser;
    private User otherUser;
    private FitnesDnevnik fitnesDnevnik;
    private StavkaDnevnika stavkaDnevnika;
    private FitnesDnevnikRequest dnevnikRequest;
    private FitnesDnevnikResponse dnevnikResponse;
    private StavkaDnevnikaRequest stavkaRequest;
    private StavkaDnevnikaResponse stavkaResponse;

    @BeforeEach
    void setUp() {
       
        authenticatedUser = new User();
        authenticatedUser.setId(1);
        authenticatedUser.setEmail("user1@example.com");

        otherUser = new User();
        otherUser.setId(2);
        otherUser.setEmail("user2@example.com");

        fitnesDnevnik = new FitnesDnevnik();
        fitnesDnevnik.setId(101L);
        fitnesDnevnik.setNaziv("Moj Dnevnik");
        fitnesDnevnik.setVezbac(authenticatedUser);

        stavkaDnevnika = new StavkaDnevnika();
        stavkaDnevnika.setId(201L);
        stavkaDnevnika.setNazivAktivnosti("Trčanje");
        stavkaDnevnika.setDnevnik(fitnesDnevnik);

        dnevnikRequest = new FitnesDnevnikRequest();
        dnevnikRequest.setNaslov("Novi Dnevnik");

        dnevnikResponse = new FitnesDnevnikResponse();
        dnevnikResponse.setId(101L);
        dnevnikResponse.setNaslov("Moj Dnevnik");

        stavkaRequest = new StavkaDnevnikaRequest();
        stavkaRequest.setNazivAktivnosti("Plivanje");

        stavkaResponse = new StavkaDnevnikaResponse();
        stavkaResponse.setId(202L);
        stavkaResponse.setNazivAktivnosti("Plivanje");
    }

   
    private void mockAuthenticatedUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(authenticatedUser.getEmail());
        when(userRepository.findByEmail(authenticatedUser.getEmail())).thenReturn(Optional.of(authenticatedUser));
        SecurityContextHolder.setContext(securityContext);
    }
    
   

    @Test
    void testGetDnevnikById_Success() {
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockAuthenticatedUser();

           
            fitnesDnevnik.setStavkeDnevnika(new ArrayList<>(List.of(
                new StavkaDnevnika(1L, fitnesDnevnik, LocalDate.of(2023, 1, 2), "Druga aktivnost", "Komentar 2", null, null),
                new StavkaDnevnika(2L, fitnesDnevnik, LocalDate.of(2023, 1, 1), "Prva aktivnost", "Komentar 1", null, null)
            )));

            when(fitnesDnevnikRepository.findById(101L)).thenReturn(Optional.of(fitnesDnevnik));
            when(fitnesDnevnikMapper.toFitnesDnevnikResponse(any(FitnesDnevnik.class))).thenReturn(dnevnikResponse);

            FitnesDnevnikResponse result = fitnesDnevnikService.getDnevnikById(101L);

            assertNotNull(result);
            assertEquals(101L, result.getId());
            verify(fitnesDnevnikRepository, times(1)).findById(101L);
            verify(fitnesDnevnikMapper, times(1)).toFitnesDnevnikResponse(fitnesDnevnik);
           
            assertEquals("Druga aktivnost", fitnesDnevnik.getStavkeDnevnika().get(0).getNazivAktivnosti());
        }
    }

    @Test
    void testGetDnevnikById_UnauthorizedAccess() {
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockAuthenticatedUser();

            FitnesDnevnik otherUserDnevnik = new FitnesDnevnik();
            otherUserDnevnik.setVezbac(otherUser);

            when(fitnesDnevnikRepository.findById(102L)).thenReturn(Optional.of(otherUserDnevnik));

            assertThrows(UnauthorizedAccessException.class, () -> fitnesDnevnikService.getDnevnikById(102L));
        }
    }

    @Test
    void testGetDnevnikById_NotFound() {
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockAuthenticatedUser();

            when(fitnesDnevnikRepository.findById(999L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> fitnesDnevnikService.getDnevnikById(999L));
            assertEquals("Dnevnik nije pronađen sa id : '999'", exception.getMessage());

        }
    }

 

    @Test
    void testCreateDnevnik_Success() {
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockAuthenticatedUser();

            when(fitnesDnevnikMapper.toFitnesDnevnik(dnevnikRequest)).thenReturn(fitnesDnevnik);
            when(fitnesDnevnikRepository.save(fitnesDnevnik)).thenReturn(fitnesDnevnik);
            when(fitnesDnevnikMapper.toFitnesDnevnikResponse(fitnesDnevnik)).thenReturn(dnevnikResponse);

            FitnesDnevnikResponse result = fitnesDnevnikService.createDnevnik(dnevnikRequest);

            assertNotNull(result);
            assertEquals("Moj Dnevnik", result.getNaslov());
            verify(fitnesDnevnikRepository, times(1)).save(fitnesDnevnik);
         
            assertEquals(authenticatedUser, fitnesDnevnik.getVezbac());
        }
    }

    

    @Test
    void testAddStavkaToDnevnik_Success() {
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockAuthenticatedUser();

            when(fitnesDnevnikRepository.findById(101L)).thenReturn(Optional.of(fitnesDnevnik));
            when(stavkaDnevnikaMapper.toStavkaDnevnika(stavkaRequest)).thenReturn(stavkaDnevnika);
            when(stavkaDnevnikaRepository.save(stavkaDnevnika)).thenReturn(stavkaDnevnika);
            when(stavkaDnevnikaMapper.toStavkaDnevnikaResponse(stavkaDnevnika)).thenReturn(stavkaResponse);

            StavkaDnevnikaResponse result = fitnesDnevnikService.addStavkaToDnevnik(101L, stavkaRequest);

            assertNotNull(result);
            assertEquals("Plivanje", result.getNazivAktivnosti());
            verify(stavkaDnevnikaRepository, times(1)).save(stavkaDnevnika);
         
            assertEquals(fitnesDnevnik, stavkaDnevnika.getDnevnik());
        }
    }

    @Test
    void testAddStavkaToDnevnik_UnauthorizedAccess() {
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockAuthenticatedUser();

            FitnesDnevnik otherUserDnevnik = new FitnesDnevnik();
            otherUserDnevnik.setId(102L);
            otherUserDnevnik.setVezbac(otherUser);

            when(fitnesDnevnikRepository.findById(102L)).thenReturn(Optional.of(otherUserDnevnik));

            assertThrows(UnauthorizedAccessException.class, () -> fitnesDnevnikService.addStavkaToDnevnik(102L, stavkaRequest));
        }
    }

    @Test
    void testAddStavkaToDnevnik_NotFound() {
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockAuthenticatedUser();

            when(fitnesDnevnikRepository.findById(999L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> fitnesDnevnikService.addStavkaToDnevnik(999L, stavkaRequest));
            assertEquals("Dnevnik nije pronađen sa id : '999'", exception.getMessage());
        }
    }
}
