/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Integer id;
    private String email; 
     private RoleResponse role; 
    private LocalDateTime datumRegistracije; 
}