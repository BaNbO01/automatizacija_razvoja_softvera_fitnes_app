/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.service;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
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

@Service
public class FitnesDnevnikService {

    private final FitnesDnevnikRepository fitnesDnevnikRepository;
    private final StavkaDnevnikaRepository stavkaDnevnikaRepository;
    private final UserRepository userRepository;
    private final FitnesDnevnikMapper fitnesDnevnikMapper;
    private final StavkaDnevnikaMapper stavkaDnevnikaMapper;

    public FitnesDnevnikService(FitnesDnevnikRepository fitnesDnevnikRepository,
                                StavkaDnevnikaRepository stavkaDnevnikaRepository,
                                UserRepository userRepository,
                                FitnesDnevnikMapper fitnesDnevnikMapper,
                                StavkaDnevnikaMapper stavkaDnevnikaMapper) {
        this.fitnesDnevnikRepository = fitnesDnevnikRepository;
        this.stavkaDnevnikaRepository = stavkaDnevnikaRepository;
        this.userRepository = userRepository;
        this.fitnesDnevnikMapper = fitnesDnevnikMapper;
        this.stavkaDnevnikaMapper = stavkaDnevnikaMapper;
    }

 
    @Transactional(readOnly = true)
    public FitnesDnevnikResponse getDnevnikById(Long id) {
        User currentUser = getAuthenticatedUser();

        FitnesDnevnik dnevnik = fitnesDnevnikRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dnevnik", "id", id));

       
        if(dnevnik.getVezbac().getId() != currentUser.getId()) {
            throw new UnauthorizedAccessException("Nemate dozvolu za pregled dnevnika.");
        }

       
        dnevnik.getStavkeDnevnika().sort(Comparator.comparing(StavkaDnevnika::getDatum).reversed());

        return fitnesDnevnikMapper.toFitnesDnevnikResponse(dnevnik);
    }

  
    @Transactional
    public FitnesDnevnikResponse createDnevnik(FitnesDnevnikRequest request) {
        User currentUser = getAuthenticatedUser();

        FitnesDnevnik dnevnik = fitnesDnevnikMapper.toFitnesDnevnik(request);
        dnevnik.setVezbac(currentUser); 
        FitnesDnevnik savedDnevnik = fitnesDnevnikRepository.save(dnevnik);
        return fitnesDnevnikMapper.toFitnesDnevnikResponse(savedDnevnik);
    }

   
    @Transactional
    public StavkaDnevnikaResponse addStavkaToDnevnik(Long dnevnikId, StavkaDnevnikaRequest request) {
        User currentUser = getAuthenticatedUser();

        FitnesDnevnik dnevnik = fitnesDnevnikRepository.findById(dnevnikId)
                .orElseThrow(() -> new ResourceNotFoundException("Dnevnik", "id", dnevnikId));

        if (
            dnevnik.getVezbac().getId() != currentUser.getId()) { 
            throw new UnauthorizedAccessException("Nemate dozvolu za dodavanje stavke u ovaj dnevnik.");
        }
        System.out.println(dnevnik.getVezbac().getId());
        System.out.println(currentUser.getId());

        StavkaDnevnika stavka = stavkaDnevnikaMapper.toStavkaDnevnika(request);
        stavka.setDnevnik(dnevnik); // Povezivanje stavke sa dnevnikom

        StavkaDnevnika savedStavka = stavkaDnevnikaRepository.save(stavka);
        return stavkaDnevnikaMapper.toStavkaDnevnikaResponse(savedStavka);
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