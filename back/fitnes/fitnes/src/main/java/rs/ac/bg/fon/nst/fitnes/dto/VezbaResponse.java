/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class VezbaResponse {
    private Long id;
    private String naziv;
    private String opis;
    private String slika; 
    private String misiciNaKojeUtice;
    private String savet;
    private Integer preporuceniBrojSerija;
    private Integer preporuceniBrojPonavljanja;
    private String videoUrl; 
    private KategorijaVezbeResponse kategorija;
}
