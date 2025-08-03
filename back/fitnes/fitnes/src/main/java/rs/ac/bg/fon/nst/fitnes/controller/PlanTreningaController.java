/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.controller;



import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.bg.fon.nst.fitnes.dto.PlanTreningaRequest;
import rs.ac.bg.fon.nst.fitnes.dto.PlanTreningaResponse;
import rs.ac.bg.fon.nst.fitnes.service.PlanTreningaService;



@RestController
@RequestMapping("/api/plan-treninga")
public class PlanTreningaController {

    private final PlanTreningaService planTreningaService;

    public PlanTreningaController(PlanTreningaService planTreningaService) {
        this.planTreningaService = planTreningaService;
    }

    
    @GetMapping
    public ResponseEntity<Page<PlanTreningaResponse>> getAllPlanoviTreninga(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PlanTreningaResponse> planovi = planTreningaService.getAllPlanoviTreninga(page, size);
        return ResponseEntity.ok(planovi);
    }

   
    @GetMapping("/{id}")
    public ResponseEntity<PlanTreningaResponse> getPlanTreningaById(@PathVariable Long id) {
        PlanTreningaResponse plan = planTreningaService.getPlanTreningaById(id);
        return ResponseEntity.ok(plan);
    }

    
    @PostMapping
    public ResponseEntity<PlanTreningaResponse> createPlanTreninga(@Valid @RequestBody PlanTreningaRequest request) {
        PlanTreningaResponse createdPlan = planTreningaService.createPlanTreninga(request);
        return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
    }
    
   
}

