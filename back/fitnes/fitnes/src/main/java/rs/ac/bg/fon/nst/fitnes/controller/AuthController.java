/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.controller;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.bg.fon.nst.fitnes.dto.AuthResponse;
import rs.ac.bg.fon.nst.fitnes.dto.LoginRequest;
import rs.ac.bg.fon.nst.fitnes.dto.RegisterRequest;
import rs.ac.bg.fon.nst.fitnes.dto.UserResponse;
import rs.ac.bg.fon.nst.fitnes.service.auth.AuthService;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

   
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse userResponse = authService.registerUser(request);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
        
    }

   
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.loginUser(request);
        
        UserResponse userResponse = authService.getUserByEmail(request.getEmail()); 

        AuthResponse authResponse = new AuthResponse(
                "Uspešna prijava!",
                true,
                userResponse,
                token,
                "Bearer",
                userResponse.getRole().getRole()
        );
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        authService.logoutUser();
        return ResponseEntity.ok("Uspešno ste se odjavili!");
    }
}
