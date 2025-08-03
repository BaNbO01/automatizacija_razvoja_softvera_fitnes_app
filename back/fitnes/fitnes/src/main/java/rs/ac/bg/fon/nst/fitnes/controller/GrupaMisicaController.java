/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.controller;



import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Optional;
import rs.ac.bg.fon.nst.fitnes.dto.GrupaMisicaRequest;
import rs.ac.bg.fon.nst.fitnes.dto.GrupaMisicaResponse;
import rs.ac.bg.fon.nst.fitnes.service.GrupaMisicaService;


@RestController
@RequestMapping("/api/grupe-misica")
public class GrupaMisicaController {

    private final GrupaMisicaService grupaMisicaService;

    public GrupaMisicaController(GrupaMisicaService grupaMisicaService) {
        this.grupaMisicaService = grupaMisicaService;
    }

    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // Očekuje multipart/form-data
    public ResponseEntity<GrupaMisicaResponse> createGrupaMisica(
            @Valid @RequestPart("request") GrupaMisicaRequest request, // Podaci kao JSON deo
            @RequestPart("slika") MultipartFile slika) { // Fajl kao deo
        GrupaMisicaResponse createdGrupa = grupaMisicaService.createGrupaMisica(request, slika);
        return new ResponseEntity<>(createdGrupa, HttpStatus.CREATED);
    }

    
    @GetMapping
    public ResponseEntity<List<GrupaMisicaResponse>> getAllGrupeMisica() {
        List<GrupaMisicaResponse> grupeMisica = grupaMisicaService.getAllGrupeMisica();
        return ResponseEntity.ok(grupeMisica);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<GrupaMisicaResponse> getGrupaMisicaById(
            @PathVariable Long id,
            @RequestParam(required = false) Optional<Long> kategorijaId) {
        GrupaMisicaResponse grupaMisica = grupaMisicaService.getGrupaMisicaById(id, kategorijaId);
        return ResponseEntity.ok(grupaMisica);
    }
}

