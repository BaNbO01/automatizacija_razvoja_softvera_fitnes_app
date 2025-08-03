/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.service;



import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import rs.ac.bg.fon.nst.fitnes.domain.GrupaMisica;
import rs.ac.bg.fon.nst.fitnes.domain.User;
import rs.ac.bg.fon.nst.fitnes.dto.GrupaMisicaRequest;
import rs.ac.bg.fon.nst.fitnes.dto.GrupaMisicaResponse;
import rs.ac.bg.fon.nst.fitnes.exception.DuplicateEntryException;
import rs.ac.bg.fon.nst.fitnes.exception.ResourceNotFoundException;
import rs.ac.bg.fon.nst.fitnes.exception.UnauthorizedAccessException;
import rs.ac.bg.fon.nst.fitnes.mapper.GrupaMisicaMapper;
import rs.ac.bg.fon.nst.fitnes.repo.GrupaMisicaRepository;
import rs.ac.bg.fon.nst.fitnes.repo.UserRepository;
import rs.ac.bg.fon.nst.fitnes.service.file.FileStorageService;

@Service
public class GrupaMisicaService {

    private final GrupaMisicaRepository grupaMisicaRepository;
    private final GrupaMisicaMapper grupaMisicaMapper;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository; // Potrebno za proveru uloge

    public GrupaMisicaService(GrupaMisicaRepository grupaMisicaRepository,
                              GrupaMisicaMapper grupaMisicaMapper,
                              FileStorageService fileStorageService,
                              UserRepository userRepository) {
        this.grupaMisicaRepository = grupaMisicaRepository;
        this.grupaMisicaMapper = grupaMisicaMapper;
        this.fileStorageService = fileStorageService;
        this.userRepository = userRepository;
    }

   
    @Transactional
    public GrupaMisicaResponse createGrupaMisica(GrupaMisicaRequest request, MultipartFile slika) {
        User currentUser = getAuthenticatedUser();
      
        Optional<GrupaMisica> existingGrupa = grupaMisicaRepository.findAll().stream() 
                .filter(g -> g.getNaziv().equalsIgnoreCase(request.getNaziv()))
                .findFirst();
        if (existingGrupa.isPresent()) {
            throw new DuplicateEntryException("naziv", request.getNaziv());
        }

        GrupaMisica grupaMisica = grupaMisicaMapper.toGrupaMisica(request);

        if (slika != null && !slika.isEmpty()) {
            String fileName = fileStorageService.storeFile(slika, request.getNaziv());
            grupaMisica.setSlika(fileName);
        }

        GrupaMisica savedGrupa = grupaMisicaRepository.save(grupaMisica);
        return grupaMisicaMapper.toGrupaMisicaResponse(savedGrupa);
    }

   
    @Transactional(readOnly = true)
    public List<GrupaMisicaResponse> getAllGrupeMisica() {
        List<GrupaMisica> grupeMisica = grupaMisicaRepository.findAll();
        return grupaMisicaMapper.toGrupaMisicaResponseList(grupeMisica);
    }


    @Transactional(readOnly = true)
    public GrupaMisicaResponse getGrupaMisicaById(Long id, Optional<Long> kategorijaId) {
        GrupaMisica grupaMisica = grupaMisicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grupa mišića", "id", id));

        if (kategorijaId.isPresent()) {
            grupaMisica.setVezbe(
                    grupaMisica.getVezbe().stream()
                            .filter(v -> v.getKategorija().getId().equals(kategorijaId.get()))
                            .collect(Collectors.toList())
            );
        }

        return grupaMisicaMapper.toGrupaMisicaResponse(grupaMisica);
    }

  
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedAccessException("Korisnik nije autentifikovan.");
        }
        String email = authentication.getName(); 
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik sa emailom " + email + " nije pronađen."));
    }
}
