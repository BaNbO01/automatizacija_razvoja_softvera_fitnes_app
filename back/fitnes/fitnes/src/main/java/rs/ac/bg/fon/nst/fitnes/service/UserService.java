/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.bg.fon.nst.fitnes.domain.User;
import rs.ac.bg.fon.nst.fitnes.dto.FitnesDnevnikResponse;
import rs.ac.bg.fon.nst.fitnes.dto.UserResponse;
import rs.ac.bg.fon.nst.fitnes.exception.ResourceNotFoundException;
import rs.ac.bg.fon.nst.fitnes.exception.UnauthorizedAccessException;
import rs.ac.bg.fon.nst.fitnes.mapper.FitnesDnevnikMapper;
import rs.ac.bg.fon.nst.fitnes.mapper.UserMapper;
import rs.ac.bg.fon.nst.fitnes.repo.FitnesDnevnikRepository;
import rs.ac.bg.fon.nst.fitnes.repo.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FitnesDnevnikRepository fitnesDnevnikRepository;
    private final UserMapper userMapper;
    private final FitnesDnevnikMapper fitnesDnevnikMapper;

    public UserService(UserRepository userRepository, FitnesDnevnikRepository fitnesDnevnikRepository,
                       UserMapper userMapper, FitnesDnevnikMapper fitnesDnevnikMapper) {
        this.userRepository = userRepository;
        this.fitnesDnevnikRepository = fitnesDnevnikRepository;
        this.userMapper = userMapper;
        this.fitnesDnevnikMapper = fitnesDnevnikMapper;
    }

  
    @Transactional(readOnly = true)
    public Page<FitnesDnevnikResponse> getLoggedInUserDnevnici(int page, int size) {
        User currentUser = getAuthenticatedUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return fitnesDnevnikRepository.findByVezbac(currentUser, pageable)
                .map(fitnesDnevnikMapper::toFitnesDnevnikResponse);
    }

   
    @Transactional(readOnly = true)
    public Page<UserResponse> getTrainers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByRole_Role("TRENER", pageable)
                .map(userMapper::toUserResponse);
    }

   
    @Transactional
    public void deleteTrainer(Integer id) {
        User trainer = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trener", "id", id));

      
        userRepository.delete(trainer);
    }

  
    @Transactional(readOnly = true)
    public Page<UserResponse> getVezbaci(int page, int size) {
        // Provera uloge "ADMIN" je već urađena u Spring Security konfiguraciji za ovu rutu.
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByRole_Role("VEZBAC", pageable)
                .map(userMapper::toUserResponse);
    }

  
    @Transactional
    public void deleteVezbac(Integer id) {
        // Provera uloge "ADMIN" je već urađena u Spring Security konfiguraciji za ovu rutu.
        User vezbac = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vežbač", "id", id));

        
        userRepository.delete(vezbac);
    }

  
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedAccessException("Korisnik nije autentifikovan.");
        }
        String email = authentication.getName(); // Email je username
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik sa emailom " + email + " nije pronađen."));
    }
}
