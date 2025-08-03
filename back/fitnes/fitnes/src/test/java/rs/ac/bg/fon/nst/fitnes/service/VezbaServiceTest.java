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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import rs.ac.bg.fon.nst.fitnes.domain.GrupaMisica;
import rs.ac.bg.fon.nst.fitnes.domain.KategorijaVezbe;
import rs.ac.bg.fon.nst.fitnes.domain.User;
import rs.ac.bg.fon.nst.fitnes.domain.Vezba;
import rs.ac.bg.fon.nst.fitnes.dto.VezbaRequest;
import rs.ac.bg.fon.nst.fitnes.dto.VezbaResponse;
import rs.ac.bg.fon.nst.fitnes.exception.ResourceNotFoundException;
import rs.ac.bg.fon.nst.fitnes.exception.UnauthorizedAccessException;
import rs.ac.bg.fon.nst.fitnes.mapper.VezbaMapper;
import rs.ac.bg.fon.nst.fitnes.repo.GrupaMisicaRepository;
import rs.ac.bg.fon.nst.fitnes.repo.KategorijaVezbeRepository;
import rs.ac.bg.fon.nst.fitnes.repo.UserRepository;
import rs.ac.bg.fon.nst.fitnes.repo.VezbaRepository;
import rs.ac.bg.fon.nst.fitnes.service.file.FileStorageService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class VezbaServiceTest {

    @InjectMocks
    private VezbaService vezbaService;

    
    @Mock
    private VezbaRepository vezbaRepository;
    @Mock
    private GrupaMisicaRepository grupaMisicaRepository;
    @Mock
    private KategorijaVezbeRepository kategorijaVezbeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private VezbaMapper vezbaMapper;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    private User mockUser;
    private GrupaMisica mockGrupaMisica;
    private KategorijaVezbe mockKategorijaVezbe;
    private Vezba mockVezba;
    private VezbaRequest mockVezbaRequest;
    private VezbaResponse mockVezbaResponse;

    @BeforeEach
    void setUp() {
     
        mockUser = new User();
        mockUser.setId(1);
        mockUser.setEmail("trener@example.com");

        mockGrupaMisica = new GrupaMisica();
        mockGrupaMisica.setId(1L);
        mockGrupaMisica.setNaziv("Grudi");

        mockKategorijaVezbe = new KategorijaVezbe();
        mockKategorijaVezbe.setId(1L);
        mockKategorijaVezbe.setNaziv("Snaga");

        mockVezba = new Vezba();
        mockVezba.setId(1L);
        mockVezba.setNaziv("Bench Press");
        mockVezba.setTrener(mockUser);
        mockVezba.setGrupaMisica(mockGrupaMisica);
        mockVezba.setKategorija(mockKategorijaVezbe);

        mockVezbaRequest = new VezbaRequest();
        mockVezbaRequest.setNaziv("Bench Press");
        mockVezbaRequest.setGrupaMisicaId(1L);
        mockVezbaRequest.setKategorijaId(1L);

        mockVezbaResponse = new VezbaResponse();
        mockVezbaResponse.setId(1L);
        mockVezbaResponse.setNaziv("Bench Press");
    }

    
    private void mockAuthenticatedUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("trener@example.com");
        when(authentication.getName()).thenReturn("trener@example.com");
        when(userRepository.findByEmail("trener@example.com")).thenReturn(Optional.of(mockUser));
        SecurityContextHolder.setContext(securityContext);
    }

   

    @Test
    void testGetAllVezbe_Success() {
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockAuthenticatedUser();

            List<Vezba> vezbeList = Arrays.asList(mockVezba);
            List<VezbaResponse> responseList = Arrays.asList(mockVezbaResponse);

            when(vezbaRepository.findAll()).thenReturn(vezbeList);
            when(vezbaMapper.toVezbaResponseList(vezbeList)).thenReturn(responseList);

            List<VezbaResponse> result = vezbaService.getAllVezbe();

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Bench Press", result.get(0).getNaziv());

            verify(vezbaRepository, times(1)).findAll();
            verify(vezbaMapper, times(1)).toVezbaResponseList(vezbeList);
        }
    }

    @Test
    void testGetAllVezbe_EmptyList() {
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockAuthenticatedUser();

            when(vezbaRepository.findAll()).thenReturn(Collections.emptyList());
            when(vezbaMapper.toVezbaResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

            List<VezbaResponse> result = vezbaService.getAllVezbe();

            assertNotNull(result);
            assertEquals(0, result.size());

            verify(vezbaRepository, times(1)).findAll();
            verify(vezbaMapper, times(1)).toVezbaResponseList(Collections.emptyList());
        }
    }



    @Test
    void testGetVezbaById_Success() {
        when(vezbaRepository.findById(1L)).thenReturn(Optional.of(mockVezba));
        when(vezbaMapper.toVezbaResponse(mockVezba)).thenReturn(mockVezbaResponse);

        VezbaResponse result = vezbaService.getVezbaById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Bench Press", result.getNaziv());

        verify(vezbaRepository, times(1)).findById(1L);
        verify(vezbaMapper, times(1)).toVezbaResponse(mockVezba);
    }

    @Test
    void testGetVezbaById_NotFound() {
        when(vezbaRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> vezbaService.getVezbaById(99L));

        assertEquals("Vežba nije pronađen sa id : '99'", exception.getMessage());
        verify(vezbaRepository, times(1)).findById(99L);
        verifyNoInteractions(vezbaMapper);
    }

 

    @Test
    void testCreateVezba_Success() {
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockAuthenticatedUser();

            when(grupaMisicaRepository.findById(anyLong())).thenReturn(Optional.of(mockGrupaMisica));
            when(kategorijaVezbeRepository.findById(anyLong())).thenReturn(Optional.of(mockKategorijaVezbe));
            when(vezbaMapper.toVezba(any(VezbaRequest.class))).thenReturn(mockVezba);
            when(fileStorageService.storeFile(any(MultipartFile.class), anyString())).thenReturn("test-file.jpg");
            when(vezbaRepository.save(any(Vezba.class))).thenReturn(mockVezba);
            when(vezbaMapper.toVezbaResponse(any(Vezba.class))).thenReturn(mockVezbaResponse);

            MultipartFile slika = new MockMultipartFile("slika", "slika.jpg", "image/jpeg", "slika data".getBytes());
            MultipartFile video = new MockMultipartFile("video", "video.mp4", "video/mp4", "video data".getBytes());

            VezbaResponse result = vezbaService.createVezba(mockVezbaRequest, slika, video);

            assertNotNull(result);
            assertEquals("Bench Press", result.getNaziv());
            verify(vezbaRepository, times(1)).save(any(Vezba.class));
            verify(fileStorageService, times(2)).storeFile(any(MultipartFile.class), anyString());
        }
    }

    @Test
    void testCreateVezba_GrupaMisicaNotFound() {
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockAuthenticatedUser();

            when(grupaMisicaRepository.findById(anyLong())).thenReturn(Optional.empty());

            MultipartFile slika = new MockMultipartFile("slika", "slika.jpg", "image/jpeg", "slika data".getBytes());
            MultipartFile video = new MockMultipartFile("video", "video.mp4", "video/mp4", "video data".getBytes());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                    vezbaService.createVezba(mockVezbaRequest, slika, video));

            assertEquals("Grupa mišića nije pronađen sa id : '1'", exception.getMessage());
            verify(vezbaRepository, never()).save(any());
        }
    }



    @Test
    void testUpdateVezba_Success() {
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockAuthenticatedUser();

            mockVezba.setSlika("stara-slika/stara.jpg");
            when(vezbaRepository.findById(1L)).thenReturn(Optional.of(mockVezba));
            when(grupaMisicaRepository.findById(anyLong())).thenReturn(Optional.of(mockGrupaMisica));
            when(kategorijaVezbeRepository.findById(anyLong())).thenReturn(Optional.of(mockKategorijaVezbe));
            when(vezbaRepository.save(any(Vezba.class))).thenReturn(mockVezba);
            when(vezbaMapper.toVezbaResponse(any(Vezba.class))).thenReturn(mockVezbaResponse);
            when(fileStorageService.storeFile(any(MultipartFile.class), anyString())).thenReturn("nova-slika/nova.jpg");
            when(fileStorageService.deleteFile(anyString())).thenReturn(true);

            VezbaRequest updateRequest = new VezbaRequest();
            updateRequest.setNaziv("Updated Bench Press");
            updateRequest.setGrupaMisicaId(1L);
            updateRequest.setKategorijaId(1L);

            MultipartFile slika = new MockMultipartFile("slika", "slika.jpg", "image/jpeg", "slika data".getBytes());

            VezbaResponse result = vezbaService.updateVezba(1L, updateRequest, slika, null);

            assertNotNull(result);
            assertEquals("Bench Press", result.getNaziv()); // Maper nije implementiran u mocku
            verify(vezbaRepository, times(1)).findById(1L);
            verify(vezbaRepository, times(1)).save(any(Vezba.class));
            verify(fileStorageService, times(1)).deleteFile("stara-slika/stara.jpg");
            verify(fileStorageService, times(1)).storeFile(any(MultipartFile.class), anyString());
        }
    }
    
    @Test
    void testUpdateVezba_UnauthorizedAccess() {
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            
            
            User otherUser = new User();
            otherUser.setId(2);
            otherUser.setEmail("drugi@example.com");

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getName()).thenReturn(otherUser.getEmail());
            when(userRepository.findByEmail(otherUser.getEmail())).thenReturn(Optional.of(otherUser));
            SecurityContextHolder.setContext(securityContext);

            when(vezbaRepository.findById(1L)).thenReturn(Optional.of(mockVezba));

            VezbaRequest updateRequest = new VezbaRequest();
            UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class, () ->
                    vezbaService.updateVezba(1L, updateRequest, null, null));

            assertEquals("Nemate dozvolu za ažuriranje ove vežbe.", exception.getMessage());
            verify(vezbaRepository, times(1)).findById(1L);
            verify(vezbaRepository, never()).save(any());
        }
    }
}
