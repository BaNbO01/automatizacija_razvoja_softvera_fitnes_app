/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.controller;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import rs.ac.bg.fon.nst.fitnes.dto.KategorijaVezbeResponse;
import rs.ac.bg.fon.nst.fitnes.service.KategorijaVezbeService;


@RestController
@RequestMapping("/api/kategorije-vezbe")
public class KategorijaVezbeController {

    private final KategorijaVezbeService kategorijaVezbeService;

    public KategorijaVezbeController(KategorijaVezbeService kategorijaVezbeService) {
        this.kategorijaVezbeService = kategorijaVezbeService;
    }

   
    @GetMapping
    public ResponseEntity<List<KategorijaVezbeResponse>> getAllKategorijeVezbe() {
        List<KategorijaVezbeResponse> kategorije = kategorijaVezbeService.getAllKategorijeVezbe();
        return ResponseEntity.ok(kategorije);
    }
}

