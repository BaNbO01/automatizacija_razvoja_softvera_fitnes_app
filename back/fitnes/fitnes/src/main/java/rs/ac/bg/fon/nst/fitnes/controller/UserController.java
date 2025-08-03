/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.controller;



import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.bg.fon.nst.fitnes.dto.FitnesDnevnikResponse;
import rs.ac.bg.fon.nst.fitnes.dto.UserResponse;
import rs.ac.bg.fon.nst.fitnes.service.UserService;



@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

   
    @GetMapping("/dnevnici")
    public ResponseEntity<Page<FitnesDnevnikResponse>> getLoggedInUserDnevnici(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<FitnesDnevnikResponse> dnevnici = userService.getLoggedInUserDnevnici(page, size);
        return ResponseEntity.ok(dnevnici);
    }

  
    @GetMapping("/treneri")
    public ResponseEntity<Page<UserResponse>> getTrainers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<UserResponse> trainers = userService.getTrainers(page, size);
        return ResponseEntity.ok(trainers);
    }

   
    @DeleteMapping("/treneri/{id}")
    public ResponseEntity<String> deleteTrainer(@PathVariable Integer id) {
        userService.deleteTrainer(id);
        return ResponseEntity.ok("Trener je uspešno obrisan.");
    }

   
    @GetMapping("/vezbaci")
    public ResponseEntity<Page<UserResponse>> getVezbaci(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<UserResponse> vezbaci = userService.getVezbaci(page, size);
        return ResponseEntity.ok(vezbaci);
    }

   
    @DeleteMapping("/vezbaci/{id}")
    public ResponseEntity<String> deleteVezbac(@PathVariable Integer id) {
        userService.deleteVezbac(id);
        return ResponseEntity.ok("Vežbač je uspešno obrisan.");
    }
}
