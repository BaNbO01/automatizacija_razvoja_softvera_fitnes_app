/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Korisničko ime je obavezno.")
    private String username;

    @NotBlank(message = "Email je obavezan.")
    @Email(message = "Email mora biti validan.")
    private String email;

    @NotBlank(message = "Lozinka je obavezna.")
    @Size(min = 8, message = "Lozinka mora imati najmanje 8 karaktera.")
    private String password;

    @NotBlank(message = "Uloga je obavezna.")
    @Pattern(regexp = "admin|trener|vezbac", message = "Uloga mora biti 'admin', 'trener' ili 'vezbac'.")
    private String role;
}