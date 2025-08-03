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
import org.springframework.web.multipart.MultipartFile;
import rs.ac.bg.fon.nst.fitnes.domain.GrupaMisica;
import rs.ac.bg.fon.nst.fitnes.domain.KategorijaVezbe;
import rs.ac.bg.fon.nst.fitnes.domain.User;
import rs.ac.bg.fon.nst.fitnes.domain.Vezba;
import rs.ac.bg.fon.nst.fitnes.dto.GrupaMisicaRequest;
import rs.ac.bg.fon.nst.fitnes.dto.GrupaMisicaResponse;
import rs.ac.bg.fon.nst.fitnes.dto.VezbaResponse;
import rs.ac.bg.fon.nst.fitnes.dto.KategorijaVezbeResponse;
import rs.ac.bg.fon.nst.fitnes.exception.DuplicateEntryException;
import rs.ac.bg.fon.nst.fitnes.exception.ResourceNotFoundException;
import rs.ac.bg.fon.nst.fitnes.exception.UnauthorizedAccessException;
import rs.ac.bg.fon.nst.fitnes.mapper.GrupaMisicaMapper;
import rs.ac.bg.fon.nst.fitnes.repo.GrupaMisicaRepository;
import rs.ac.bg.fon.nst.fitnes.repo.UserRepository;
import rs.ac.bg.fon.nst.fitnes.service.file.FileStorageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Collections;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrupaMisicaServiceTest {

    @InjectMocks
    private GrupaMisicaService grupaMisicaService;

   
    @Mock
    private GrupaMisicaRepository grupaMisicaRepository;
    @Mock
    private GrupaMisicaMapper grupaMisicaMapper;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private UserRepository userRepository;

   
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private MultipartFile mockFile;

    private User authenticatedUser;
    private GrupaMisicaRequest grupaMisicaRequest;
    private GrupaMisica grupaMisica;
    private GrupaMisicaResponse grupaMisicaResponse;
    private Vezba vezba1, vezba2;
    private KategorijaVezbe kategorija1, kategorija2;
    private VezbaResponse vezbaResponse1, vezbaResponse2;
    private KategorijaVezbeResponse kategorijaVezbeResponse1, kategorijaVezbeResponse2;

    @BeforeEach
    void setUp() {
       
        authenticatedUser = new User();
        authenticatedUser.setId(1);
        authenticatedUser.setEmail("user@example.com");

        grupaMisicaRequest = new GrupaMisicaRequest("Grudi", "Opis za grudi.");

        grupaMisica = new GrupaMisica(1L, "Grudi", "Opis za grudi.", null, new ArrayList<>(), LocalDateTime.now(), LocalDateTime.now());

        grupaMisicaResponse = new GrupaMisicaResponse();
        grupaMisicaResponse.setId(1L);
        grupaMisicaResponse.setNaziv("Grudi");
        grupaMisicaResponse.setVezbe(new ArrayList<>());


        kategorija1 = new KategorijaVezbe(10L, "Snaga", new ArrayList<>(), LocalDateTime.now(), LocalDateTime.now());
        kategorija2 = new KategorijaVezbe(20L, "Kardio", new ArrayList<>(), LocalDateTime.now(), LocalDateTime.now());

        vezba1 = new Vezba(100L, "Bench Press", "Opis", "slika.jpg", "prsni misic", "savet", 4, 12, "video.mp4", grupaMisica, kategorija1, null, new ArrayList<>(), LocalDateTime.now(), LocalDateTime.now());
        vezba2 = new Vezba(200L, "Trčanje na traci", "Opis", "slika2.jpg", "noge", "savet", 3, 30, "video2.mp4", grupaMisica, kategorija2, null, new ArrayList<>(), LocalDateTime.now(), LocalDateTime.now());
        grupaMisica.getVezbe().add(vezba1);
        grupaMisica.getVezbe().add(vezba2);

        kategorijaVezbeResponse1 = new KategorijaVezbeResponse(10L, "Snaga");
        kategorijaVezbeResponse2 = new KategorijaVezbeResponse(20L, "Kardio");

        vezbaResponse1 = new VezbaResponse(100L, "Bench Press", "Opis", "slika.jpg", "prsni misic", "savet", 4, 12, "video.mp4", kategorijaVezbeResponse1);
        vezbaResponse2 = new VezbaResponse(200L, "Trčanje na traci", "Opis", "slika2.jpg", "noge", "savet", 3, 30, "video2.mp4", kategorijaVezbeResponse2);
    }

  
    private void mockAuthenticatedUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(authenticatedUser.getEmail());
        when(userRepository.findByEmail(authenticatedUser.getEmail())).thenReturn(Optional.of(authenticatedUser));
        SecurityContextHolder.setContext(securityContext);
    }
    
   

    @Test
    void testCreateGrupaMisica_Success() {
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockAuthenticatedUser();

            
            when(grupaMisicaRepository.findAll()).thenReturn(Collections.emptyList());
            
           
            when(mockFile.isEmpty()).thenReturn(false);
            when(fileStorageService.storeFile(any(MultipartFile.class), any(String.class))).thenReturn("grudi.jpg");
            
            
            when(grupaMisicaMapper.toGrupaMisica(any(GrupaMisicaRequest.class))).thenReturn(grupaMisica);
            when(grupaMisicaRepository.save(any(GrupaMisica.class))).thenReturn(grupaMisica);
            when(grupaMisicaMapper.toGrupaMisicaResponse(any(GrupaMisica.class))).thenReturn(grupaMisicaResponse);

            GrupaMisicaResponse result = grupaMisicaService.createGrupaMisica(grupaMisicaRequest, mockFile);

            assertNotNull(result);
            assertEquals("Grudi", result.getNaziv());
            verify(grupaMisicaRepository, times(1)).save(grupaMisica);
            verify(fileStorageService, times(1)).storeFile(mockFile, grupaMisicaRequest.getNaziv());
        }
    }

    @Test
    void testCreateGrupaMisica_DuplicateName() {
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockAuthenticatedUser();

            
            when(grupaMisicaRepository.findAll()).thenReturn(List.of(grupaMisica));
            
            assertThrows(DuplicateEntryException.class, () -> grupaMisicaService.createGrupaMisica(grupaMisicaRequest, mockFile));
            verify(grupaMisicaRepository, never()).save(any(GrupaMisica.class));
        }
    }

    @Test
    void testCreateGrupaMisica_Unauthorized() {
         try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(false);

            assertThrows(UnauthorizedAccessException.class, () -> grupaMisicaService.createGrupaMisica(grupaMisicaRequest, mockFile));
            verify(grupaMisicaRepository, never()).save(any(GrupaMisica.class));
        }
    }

    // --- TESTOVI ZA getAllGrupeMisica() ---

    @Test
    void testGetAllGrupeMisica_Success() {
      
        GrupaMisica grupa2 = new GrupaMisica(2L, "Ledja", "Opis", null, new ArrayList<>(), LocalDateTime.now(), LocalDateTime.now());
        GrupaMisicaResponse grupaResponse2 = new GrupaMisicaResponse(2L, "Ledja", "Opis", null, new ArrayList<>());

        List<GrupaMisica> grupe = List.of(grupaMisica, grupa2);
        List<GrupaMisicaResponse> responses = List.of(grupaMisicaResponse, grupaResponse2);
        
        when(grupaMisicaRepository.findAll()).thenReturn(grupe);
        when(grupaMisicaMapper.toGrupaMisicaResponseList(grupe)).thenReturn(responses);

        List<GrupaMisicaResponse> result = grupaMisicaService.getAllGrupeMisica();
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Grudi", result.get(0).getNaziv());
        verify(grupaMisicaRepository, times(1)).findAll();
    }

    @Test
    void testGetAllGrupeMisica_EmptyList() {
        when(grupaMisicaRepository.findAll()).thenReturn(Collections.emptyList());
        when(grupaMisicaMapper.toGrupaMisicaResponseList(anyList())).thenReturn(Collections.emptyList());

        List<GrupaMisicaResponse> result = grupaMisicaService.getAllGrupeMisica();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(grupaMisicaRepository, times(1)).findAll();
    }


  

    @Test
    void testGetGrupaMisicaById_SuccessWithoutFilter() {
        when(grupaMisicaRepository.findById(1L)).thenReturn(Optional.of(grupaMisica));
        grupaMisicaResponse.setVezbe(List.of(vezbaResponse1, vezbaResponse2));
        when(grupaMisicaMapper.toGrupaMisicaResponse(grupaMisica)).thenReturn(grupaMisicaResponse);
        
        GrupaMisicaResponse result = grupaMisicaService.getGrupaMisicaById(1L, Optional.empty());

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Grudi", result.getNaziv());
        assertEquals(2, result.getVezbe().size());
        verify(grupaMisicaRepository, times(1)).findById(1L);
    }

    @Test
    void testGetGrupaMisicaById_WithCategoryFilter_Success() {
        when(grupaMisicaRepository.findById(1L)).thenReturn(Optional.of(grupaMisica));
        
      
        GrupaMisica filteredGrupaMisica = new GrupaMisica(1L, "Grudi", "Opis za grudi.", null, List.of(vezba1), LocalDateTime.now(), LocalDateTime.now());
        GrupaMisicaResponse filteredGrupaMisicaResponse = new GrupaMisicaResponse(1L, "Grudi", "Opis za grudi.", null, List.of(vezbaResponse1));

        when(grupaMisicaMapper.toGrupaMisicaResponse(any(GrupaMisica.class))).thenReturn(filteredGrupaMisicaResponse);

        GrupaMisicaResponse result = grupaMisicaService.getGrupaMisicaById(1L, Optional.of(10L));
        
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, result.getVezbe().size());
        assertEquals("Bench Press", result.getVezbe().get(0).getNaziv());
        verify(grupaMisicaRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetGrupaMisicaById_WithCategoryFilter_NoMatchingVezba() {
        when(grupaMisicaRepository.findById(1L)).thenReturn(Optional.of(grupaMisica));
        
    
        GrupaMisica filteredGrupaMisica = new GrupaMisica(1L, "Grudi", "Opis za grudi.", null, Collections.emptyList(), LocalDateTime.now(), LocalDateTime.now());
        GrupaMisicaResponse filteredGrupaMisicaResponse = new GrupaMisicaResponse(1L, "Grudi", "Opis za grudi.", null, Collections.emptyList());

        when(grupaMisicaMapper.toGrupaMisicaResponse(any(GrupaMisica.class))).thenReturn(filteredGrupaMisicaResponse);
        
        GrupaMisicaResponse result = grupaMisicaService.getGrupaMisicaById(1L, Optional.of(30L)); // Nepostojeća kategorija
        
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertTrue(result.getVezbe().isEmpty());
        verify(grupaMisicaRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetGrupaMisicaById_NotFound() {
        when(grupaMisicaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> grupaMisicaService.getGrupaMisicaById(99L, Optional.empty()));
        verify(grupaMisicaRepository, times(1)).findById(99L);
    }
}
