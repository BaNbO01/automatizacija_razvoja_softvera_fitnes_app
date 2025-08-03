/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.service;





import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.bg.fon.nst.fitnes.domain.PlanTreninga;
import rs.ac.bg.fon.nst.fitnes.domain.PlanVezbe;
import rs.ac.bg.fon.nst.fitnes.domain.User;
import rs.ac.bg.fon.nst.fitnes.domain.Vezba;
import rs.ac.bg.fon.nst.fitnes.dto.PlanTreningaGenerationRequest;
import rs.ac.bg.fon.nst.fitnes.dto.PlanTreningaRequest;
import rs.ac.bg.fon.nst.fitnes.dto.PlanTreningaResponse;
import rs.ac.bg.fon.nst.fitnes.dto.PlanVezbeRequestItem;
import rs.ac.bg.fon.nst.fitnes.exception.ResourceNotFoundException;
import rs.ac.bg.fon.nst.fitnes.exception.UnauthorizedAccessException;
import rs.ac.bg.fon.nst.fitnes.mapper.PlanTreningaMapper;
import rs.ac.bg.fon.nst.fitnes.mapper.PlanVezbeMapper;
import rs.ac.bg.fon.nst.fitnes.repo.PlanTreningaRepository;
import rs.ac.bg.fon.nst.fitnes.repo.PlanVezbeRepository;
import rs.ac.bg.fon.nst.fitnes.repo.UserRepository;
import rs.ac.bg.fon.nst.fitnes.repo.VezbaRepository;

@Service
public class PlanTreningaService {

    private final PlanTreningaRepository planTreningaRepository;
    private final PlanVezbeRepository planVezbeRepository;
    private final VezbaRepository vezbaRepository;
    private final UserRepository userRepository;
    private final PlanTreningaMapper planTreningaMapper;
    private final PlanVezbeMapper planVezbeMapper;

    public PlanTreningaService(PlanTreningaRepository planTreningaRepository,
                               PlanVezbeRepository planVezbeRepository,
                               VezbaRepository vezbaRepository,
                               UserRepository userRepository,
                               PlanTreningaMapper planTreningaMapper,
                               PlanVezbeMapper planVezbeMapper) {
        this.planTreningaRepository = planTreningaRepository;
        this.planVezbeRepository = planVezbeRepository;
        this.vezbaRepository = vezbaRepository;
        this.userRepository = userRepository;
        this.planTreningaMapper = planTreningaMapper;
        this.planVezbeMapper = planVezbeMapper;
    }

   
    @Transactional(readOnly = true)
    public Page<PlanTreningaResponse> getAllPlanoviTreninga(int page, int size) {
        User currentUser = getAuthenticatedUser();

        Pageable pageable = PageRequest.of(page, size, Sort.by("datum").descending());
        return planTreningaRepository.findByVezbac(currentUser, pageable)
                .map(planTreningaMapper::toPlanTreningaResponse);
    }

  
    @Transactional(readOnly = true)
    public PlanTreningaResponse getPlanTreningaById(Long id) {
        User currentUser = getAuthenticatedUser();
        

        PlanTreninga planTreninga = planTreningaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan treninga", "id", id));

        if (planTreninga.getVezbac().getId() != currentUser.getId()) {
            throw new UnauthorizedAccessException("Nemate dozvolu za pregled ovog plana treninga.");
        }

        return planTreningaMapper.toPlanTreningaResponse(planTreninga);
    }

   
    @Transactional
    public PlanTreningaResponse createPlanTreninga(PlanTreningaRequest request) {
        User currentUser = getAuthenticatedUser();
      

        PlanTreninga planTreninga = planTreningaMapper.toPlanTreninga(request);
        planTreninga.setVezbac(currentUser);
        planTreninga.setDatum(LocalDateTime.now());

       
        List<PlanVezbe> planoviVezbi = new ArrayList<>();
        for (PlanVezbeRequestItem item : request.getVezbe()) {
            Vezba vezba = vezbaRepository.findById(item.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vežba", "id", item.getId()));

            PlanVezbe planVezbe = planVezbeMapper.toPlanVezbe(item);
            planVezbe.setVezba(vezba);
            planVezbe.setPlanTreninga(planTreninga); 
            planVezbe.setDatum(planTreninga.getDatum().toLocalDate()); 

            planoviVezbi.add(planVezbe);
        }
        planTreninga.setPlanoviVezbi(planoviVezbi); 

        PlanTreninga savedPlan = planTreningaRepository.save(planTreninga);
        return planTreningaMapper.toPlanTreningaResponse(savedPlan);
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
    
    @Transactional
public List<PlanTreningaResponse> generatePersonalizedPlan(PlanTreningaGenerationRequest request) {
  
    User currentUser = getAuthenticatedUser();


    List<Vezba> sveVezbe = new ArrayList<>();
    for (String grupa : request.getGrupeMisica()) {
        sveVezbe.addAll(vezbaRepository.findByGrupaMisicaNazivIgnoreCase(grupa));
    }

   
    if (sveVezbe.isEmpty()) {
        throw new ResourceNotFoundException("Nijedna vežba nije pronađena za izabrane mišićne grupe.");
    }

    List<PlanTreninga> kreiraniPlanovi = new ArrayList<>();
    int vezbePoDanu = 7;
    LocalDateTime danasnjiDatum = LocalDateTime.now();

  
    for (int dan = 0; dan < request.getBrojDana(); dan++) {
      
        PlanTreninga noviPlan = new PlanTreninga();
        noviPlan.setNaziv(request.getNazivPlana() + " - Dan " + (dan + 1));
        noviPlan.setVezbac(currentUser);
        noviPlan.setPlanoviVezbi(new ArrayList<>());
        noviPlan.setDatum(danasnjiDatum.plusDays(dan));

       
        Collections.shuffle(sveVezbe);

       
        List<Vezba> odabraneVezbeZaDan = sveVezbe.subList(0, Math.min(vezbePoDanu, sveVezbe.size()));

        
        for (Vezba vezba : odabraneVezbeZaDan) {
            PlanVezbe planVezbe = new PlanVezbe();
            planVezbe.setPlanTreninga(noviPlan);
            planVezbe.setVezba(vezba);
            planVezbe.setBrojSerija(vezba.getPreporuceniBrojSerija());
            planVezbe.setBrojPonavljanja(vezba.getPreporuceniBrojPonavljanja());
            planVezbe.setDatum(noviPlan.getDatum().toLocalDate());
            noviPlan.getPlanoviVezbi().add(planVezbe);
        }

       
        kreiraniPlanovi.add(noviPlan);
    }

    
    List<PlanTreninga> savedPlans = planTreningaRepository.saveAll(kreiraniPlanovi);
    return planTreningaMapper.toPlanTreningaResponseList(savedPlans);
}
   

}

