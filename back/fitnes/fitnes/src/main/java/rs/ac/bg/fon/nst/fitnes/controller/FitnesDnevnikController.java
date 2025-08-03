/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.controller;



import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.bg.fon.nst.fitnes.dto.FitnesDnevnikRequest;
import rs.ac.bg.fon.nst.fitnes.dto.FitnesDnevnikResponse;
import rs.ac.bg.fon.nst.fitnes.dto.StavkaDnevnikaRequest;
import rs.ac.bg.fon.nst.fitnes.dto.StavkaDnevnikaResponse;
import rs.ac.bg.fon.nst.fitnes.service.FitnesDnevnikService;



@RestController
@RequestMapping("/api/dnevnici")
public class FitnesDnevnikController {

    private final FitnesDnevnikService fitnesDnevnikService;

    public FitnesDnevnikController(FitnesDnevnikService fitnesDnevnikService) {
        this.fitnesDnevnikService = fitnesDnevnikService;
    }

   
    @GetMapping("/{id}")
    public ResponseEntity<FitnesDnevnikResponse> getDnevnikById(@PathVariable Long id) {
        FitnesDnevnikResponse dnevnik = fitnesDnevnikService.getDnevnikById(id);
        return ResponseEntity.ok(dnevnik);
    }

    
    @PostMapping
    public ResponseEntity<FitnesDnevnikResponse> createDnevnik(@Valid @RequestBody FitnesDnevnikRequest request) {
        FitnesDnevnikResponse createdDnevnik = fitnesDnevnikService.createDnevnik(request);
        return new ResponseEntity<>(createdDnevnik, HttpStatus.CREATED);
    }

    
    @PostMapping("/{id}/stavke")
    public ResponseEntity<StavkaDnevnikaResponse> addStavkaToDnevnik(
            @PathVariable Long id,
            @Valid @RequestBody StavkaDnevnikaRequest request) {
        StavkaDnevnikaResponse createdStavka = fitnesDnevnikService.addStavkaToDnevnik(id, request);
        return new ResponseEntity<>(createdStavka, HttpStatus.CREATED);
    }
}

