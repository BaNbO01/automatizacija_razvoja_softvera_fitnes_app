/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanVezbeRequestItem {
    @NotNull(message = "ID vežbe je obavezan.")
    private Long id; 
    @NotNull(message = "Broj serija je obavezan.")
    @Min(value = 1, message = "Broj serija mora biti najmanje 1.")
    private Integer brojSerija;
    @NotNull(message = "Broj ponavljanja je obavezan.")
    @Min(value = 1, message = "Broj ponavljanja mora biti najmanje 1.")
    private Integer brojPonavljanja;
}