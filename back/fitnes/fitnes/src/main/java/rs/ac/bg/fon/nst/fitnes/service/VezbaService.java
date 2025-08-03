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

@Service
public class VezbaService {

    private final VezbaRepository vezbaRepository;
    private final GrupaMisicaRepository grupaMisicaRepository;
    private final KategorijaVezbeRepository kategorijaVezbeRepository;
    private final UserRepository userRepository;
    private final VezbaMapper vezbaMapper;
    private final FileStorageService fileStorageService;

    public VezbaService(VezbaRepository vezbaRepository,
                        GrupaMisicaRepository grupaMisicaRepository,
                        KategorijaVezbeRepository kategorijaVezbeRepository,
                        UserRepository userRepository,
                        VezbaMapper vezbaMapper,
                        FileStorageService fileStorageService) {
        this.vezbaRepository = vezbaRepository;
        this.grupaMisicaRepository = grupaMisicaRepository;
        this.kategorijaVezbeRepository = kategorijaVezbeRepository;
        this.userRepository = userRepository;
        this.vezbaMapper = vezbaMapper;
        this.fileStorageService = fileStorageService;
    }

   
    @Transactional(readOnly = true)
    public List<VezbaResponse> getAllVezbe() {
        User currentUser = getAuthenticatedUser();
        List<Vezba> vezbe = vezbaRepository.findAll();
        return vezbaMapper.toVezbaResponseList(vezbe);
    }

   
    @Transactional(readOnly = true)
    public VezbaResponse getVezbaById(Long id) {
        Vezba vezba = vezbaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vežba", "id", id));
        return vezbaMapper.toVezbaResponse(vezba);
    }

    
    @Transactional
    public VezbaResponse createVezba(VezbaRequest request, MultipartFile slika, MultipartFile video) {
        User currentUser = getAuthenticatedUser();
        GrupaMisica grupaMisica = grupaMisicaRepository.findById(request.getGrupaMisicaId())
                .orElseThrow(() -> new ResourceNotFoundException("Grupa mišića", "id", request.getGrupaMisicaId()));
        KategorijaVezbe kategorijaVezbe = kategorijaVezbeRepository.findById(request.getKategorijaId())
                .orElseThrow(() -> new ResourceNotFoundException("Kategorija vežbe", "id", request.getKategorijaId()));

        Vezba vezba = vezbaMapper.toVezba(request);
        vezba.setGrupaMisica(grupaMisica);
        vezba.setKategorija(kategorijaVezbe);
        vezba.setTrener(currentUser); // Trener je trenutno autentifikovani korisnik

        if (slika != null && !slika.isEmpty()) {
            String fileName = fileStorageService.storeFile(slika, request.getNaziv() + "_slika");
            vezba.setSlika(fileName);
        }
        if (video != null && !video.isEmpty()) {
            String fileName = fileStorageService.storeFile(video, request.getNaziv() + "_video");
            vezba.setVideoUrl(fileName);
        }

        Vezba savedVezba = vezbaRepository.save(vezba);
        return vezbaMapper.toVezbaResponse(savedVezba);
    }

 
    @Transactional
    public VezbaResponse updateVezba(Long id, VezbaRequest request, MultipartFile slika, MultipartFile video) {
        Vezba vezba = vezbaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vežba", "id", id));

         User currentUser = getAuthenticatedUser();
         if(vezba.getTrener() != null && vezba.getTrener().getId() != currentUser.getId()) { 
             throw new UnauthorizedAccessException("Nemate dozvolu za ažuriranje ove vežbe.");
         }


        GrupaMisica grupaMisica = grupaMisicaRepository.findById(request.getGrupaMisicaId())
                .orElseThrow(() -> new ResourceNotFoundException("Grupa mišića", "id", request.getGrupaMisicaId()));
        KategorijaVezbe kategorijaVezbe = kategorijaVezbeRepository.findById(request.getKategorijaId())
                .orElseThrow(() -> new ResourceNotFoundException("Kategorija vežbe", "id", request.getKategorijaId()));

        vezbaMapper.updateVezbaFromRequest(request, vezba); 
        vezba.setGrupaMisica(grupaMisica);
        vezba.setKategorija(kategorijaVezbe);

      
        if (slika != null && !slika.isEmpty()) {
          
            if (vezba.getSlika() != null && !vezba.getSlika().isEmpty()) {
                fileStorageService.deleteFile(vezba.getSlika());
            }
            String newFileName = fileStorageService.storeFile(slika, request.getNaziv() + "_slika");
            vezba.setSlika(newFileName);
        }

     
        if (video != null && !video.isEmpty()) {
           
            if (vezba.getVideoUrl() != null && !vezba.getVideoUrl().isEmpty()) {
                fileStorageService.deleteFile(vezba.getVideoUrl());
            }
            String newFileName = fileStorageService.storeFile(video, request.getNaziv() + "_video");
            vezba.setVideoUrl(newFileName);
        }

        Vezba updatedVezba = vezbaRepository.save(vezba);
        return vezbaMapper.toVezbaResponse(updatedVezba);
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

