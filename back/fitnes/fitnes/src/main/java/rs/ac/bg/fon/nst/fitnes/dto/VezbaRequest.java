/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class VezbaRequest {
    @NotBlank(message = "Naziv je obavezan.")
    private String naziv;
    @NotBlank(message = "Opis je obavezan.")
    private String opis;
    @NotBlank(message = "Mišići na koje utiče su obavezni.")
    private String misiciNaKojeUtice;
    @NotBlank(message = "Savet je obavezan.")
    private String savet;
    @NotNull(message = "Preporučeni broj serija je obavezan.")
    @Min(value = 1, message = "Broj serija mora biti najmanje 1.")
    private Integer preporuceniBrojSerija;
    @NotNull(message = "Preporučeni broj ponavljanja je obavezan.")
    @Min(value = 1, message = "Broj ponavljanja mora biti najmanje 1.")
    private Integer preporuceniBrojPonavljanja;
    @NotNull(message = "ID grupe mišića je obavezan.")
    private Long grupaMisicaId;
    @NotNull(message = "ID kategorije je obavezan.")
    private Long kategorijaId;
    
}
