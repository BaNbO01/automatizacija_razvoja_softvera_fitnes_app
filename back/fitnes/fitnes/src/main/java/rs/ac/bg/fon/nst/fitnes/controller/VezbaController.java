/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.controller;


import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import rs.ac.bg.fon.nst.fitnes.dto.VezbaRequest;
import rs.ac.bg.fon.nst.fitnes.dto.VezbaResponse;
import rs.ac.bg.fon.nst.fitnes.service.VezbaService;
import rs.ac.bg.fon.nst.fitnes.service.file.FileStorageService;


@RestController
@RequestMapping("/api/vezbe")
public class VezbaController {

    private final VezbaService vezbaService;
    private final FileStorageService fileStorageService; 

    public VezbaController(VezbaService vezbaService, FileStorageService fileStorageService) {
        this.vezbaService = vezbaService;
        this.fileStorageService = fileStorageService;
    }

 
    @GetMapping("/video/{id}")
    public ResponseEntity<Resource> streamVideo(@PathVariable Long id) {
        VezbaResponse vezba = vezbaService.getVezbaById(id); 
        Resource videoFile = fileStorageService.loadFileAsResource(vezba.getVideoUrl().replace("/uploads/", "")); 

        String contentType = "video/mp4"; 
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + videoFile.getFilename() + "\"")
                .body(videoFile);
    }


    @GetMapping
    public ResponseEntity<List<VezbaResponse>> getAllVezbe() {
        List<VezbaResponse> vezbe = vezbaService.getAllVezbe();
        return ResponseEntity.ok(vezbe);
    }

   
    @GetMapping("/{id}")
    public ResponseEntity<VezbaResponse> getVezbaById(@PathVariable Long id) {
        VezbaResponse vezba = vezbaService.getVezbaById(id);
        return ResponseEntity.ok(vezba);
    }

   
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VezbaResponse> createVezba(
            @Valid @RequestPart("request") VezbaRequest request,
            @RequestPart("slika") MultipartFile slika,
            @RequestPart("video") MultipartFile video) {
        VezbaResponse createdVezba = vezbaService.createVezba(request, slika, video);
        return new ResponseEntity<>(createdVezba, HttpStatus.CREATED);
    }

  
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VezbaResponse> updateVezba(
            @PathVariable Long id,
            @Valid @RequestPart("request") VezbaRequest request,
            @RequestPart(value = "slika", required = false) MultipartFile slika, 
            @RequestPart(value = "video", required = false) MultipartFile video) { 
        VezbaResponse updatedVezba = vezbaService.updateVezba(id, request, slika, video);
        return ResponseEntity.ok(updatedVezba);
    }
}

