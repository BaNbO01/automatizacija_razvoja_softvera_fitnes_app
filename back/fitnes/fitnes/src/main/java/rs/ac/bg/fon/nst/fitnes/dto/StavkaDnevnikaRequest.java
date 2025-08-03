/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class StavkaDnevnikaRequest {
    @NotNull(message = "Datum je obavezan.")
    private LocalDate datum;
    @NotBlank(message = "Naziv aktivnosti je obavezan.")
    private String nazivAktivnosti;
    @NotBlank(message = "Komentar je obavezan.")
    private String komentar;
}
